package com.app.app.service;

import com.app.app.model.AsientoContable;
import com.app.app.model.DetalleAsiento;
import com.app.app.model.PlanCuentas;
import com.app.app.model.Usuario;
import com.app.app.repository.AsientoContableRepository;
import com.app.app.repository.DetalleAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsientoContableService {

    @Autowired
    private AsientoContableRepository repository;

    @Autowired
    private DetalleAsientoRepository detalleRepository;

    /**
     * Listar todos los asientos de un usuario
     */
    public List<AsientoContable> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuarioOrderByFechaDescNumeroAsientoDesc(usuario);
    }

    /**
     * Listar asientos por estado
     */
    public List<AsientoContable> listarPorEstado(Usuario usuario, String estado) {
        return repository.findByUsuarioAndEstadoOrderByFechaDescNumeroAsientoDesc(usuario, estado);
    }

    /**
     * Listar asientos por periodo
     */
    public List<AsientoContable> listarPorPeriodo(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findByUsuarioAndFechaBetweenOrderByFechaAscNumeroAsientoAsc(usuario, fechaInicio, fechaFin);
    }

    /**
     * Buscar asiento por ID
     */
    public Optional<AsientoContable> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Buscar asiento por número
     */
    public Optional<AsientoContable> buscarPorNumero(Usuario usuario, Long numeroAsiento) {
        return repository.findByUsuarioAndNumeroAsiento(usuario, numeroAsiento);
    }

    /**
     * Obtener el siguiente número de asiento
     */
    public Long obtenerSiguienteNumero(Usuario usuario) {
        Long ultimoNumero = repository.obtenerUltimoNumeroAsiento(usuario);
        return (ultimoNumero == null) ? 1L : ultimoNumero + 1;
    }

    /**
     * Crear un nuevo asiento (en estado BORRADOR)
     */
    @Transactional
    public AsientoContable crear(AsientoContable asiento) {
        // Asignar número automático si no tiene
        if (asiento.getNumeroAsiento() == null) {
            asiento.setNumeroAsiento(obtenerSiguienteNumero(asiento.getUsuario()));
        }

        // Validar que no exista el número
        if (repository.findByUsuarioAndNumeroAsiento(asiento.getUsuario(), asiento.getNumeroAsiento()).isPresent()) {
            throw new RuntimeException("Ya existe un asiento con el número: " + asiento.getNumeroAsiento());
        }

        // Establecer estado inicial
        asiento.setEstado("BORRADOR");

        // Guardar asiento
        AsientoContable asientoGuardado = repository.save(asiento);

        // Guardar detalles si existen
        if (asiento.getDetalles() != null && !asiento.getDetalles().isEmpty()) {
            for (DetalleAsiento detalle : asiento.getDetalles()) {
                detalle.setAsiento(asientoGuardado);
            }
            detalleRepository.saveAll(asiento.getDetalles());
        }

        asientoGuardado.recalcularTotales();
        return repository.save(asientoGuardado);
    }

    /**
     * Agregar detalle a un asiento
     */
    @Transactional
    public AsientoContable agregarDetalle(Long asientoId, DetalleAsiento detalle) {
        AsientoContable asiento = repository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!asiento.getEstado().equals("BORRADOR")) {
            throw new RuntimeException("Solo se pueden modificar asientos en borrador");
        }

        detalle.setAsiento(asiento);
        detalle.setOrden(asiento.getDetalles().size() + 1);
        detalleRepository.save(detalle);

        asiento.agregarDetalle(detalle);
        return repository.save(asiento);
    }

    /**
     * Eliminar detalle de un asiento
     */
    @Transactional
    public AsientoContable eliminarDetalle(Long asientoId, Long detalleId) {
        AsientoContable asiento = repository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!asiento.getEstado().equals("BORRADOR")) {
            throw new RuntimeException("Solo se pueden modificar asientos en borrador");
        }

        DetalleAsiento detalle = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        asiento.removerDetalle(detalle);
        detalleRepository.delete(detalle);

        return repository.save(asiento);
    }

    /**
     * Contabilizar asiento (hacerlo definitivo)
     */
    @Transactional
    public AsientoContable contabilizar(Long asientoId) {
        AsientoContable asiento = repository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        asiento.recalcularTotales();

        if (!asiento.puedeSerContabilizado()) {
            throw new RuntimeException("El asiento no está cuadrado (Débito = Crédito) o no tiene detalles");
        }

        asiento.contabilizar();
        return repository.save(asiento);
    }

    /**
     * Anular asiento
     */
    @Transactional
    public AsientoContable anular(Long asientoId) {
        AsientoContable asiento = repository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        asiento.anular();
        return repository.save(asiento);
    }

    /**
     * Eliminar asiento (solo si está en borrador)
     */
    @Transactional
    public void eliminar(Long asientoId) {
        AsientoContable asiento = repository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!asiento.getEstado().equals("BORRADOR")) {
            throw new RuntimeException("Solo se pueden eliminar asientos en borrador");
        }

        repository.delete(asiento);
    }

    /**
     * Obtener estadísticas de asientos
     */
    public long contarPorEstado(Usuario usuario, String estado) {
        return repository.countByUsuarioAndEstado(usuario, estado);
    }

    /**
     * Obtener Libro Diario (asientos contabilizados en orden cronológico)
     */
    public List<AsientoContable> obtenerLibroDiario(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findAsientosContabilizadosPorPeriodo(usuario, fechaInicio, fechaFin);
    }

    /**
     * Obtener Libro Mayor por cuenta
     */
    public List<DetalleAsiento> obtenerLibroMayorCuenta(PlanCuentas cuenta, LocalDate fechaInicio, LocalDate fechaFin) {
        return detalleRepository.findByCuentaYPeriodo(cuenta, fechaInicio, fechaFin);
    }

    /**
     * Calcular saldo de una cuenta en un periodo
     */
    public BigDecimal calcularSaldoCuenta(PlanCuentas cuenta, LocalDate fechaInicio, LocalDate fechaFin) {
        List<DetalleAsiento> movimientos = detalleRepository.findByCuentaYPeriodo(cuenta, fechaInicio, fechaFin);

        BigDecimal totalDebito = movimientos.stream()
                .map(DetalleAsiento::getDebito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredito = movimientos.stream()
                .map(DetalleAsiento::getCredito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Si la cuenta es de naturaleza DEBITO, el saldo es (Débito - Crédito)
        // Si es de naturaleza CREDITO, el saldo es (Crédito - Débito)
        if ("DEBITO".equals(cuenta.getNaturaleza())) {
            return totalDebito.subtract(totalCredito);
        } else {
            return totalCredito.subtract(totalDebito);
        }
    }

    /**
     * Calcular saldo de una cuenta hasta una fecha específica
     */
    public BigDecimal calcularSaldoCuentaHastaFecha(PlanCuentas cuenta, LocalDate fechaCorte) {
        return calcularSaldoCuenta(cuenta, LocalDate.of(1900, 1, 1), fechaCorte);
    }
}
