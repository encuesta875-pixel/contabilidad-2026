package com.app.app.service;

import com.app.app.model.PlanCuentas;
import com.app.app.model.Usuario;
import com.app.app.repository.PlanCuentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlanCuentasService {

    @Autowired
    private PlanCuentasRepository repository;

    /**
     * Listar todas las cuentas de un usuario
     */
    public List<PlanCuentas> listarPorUsuario(Usuario usuario) {
        return repository.findByUsuarioOrderByCodigoAsc(usuario);
    }

    /**
     * Listar solo cuentas activas
     */
    public List<PlanCuentas> listarActivasPorUsuario(Usuario usuario) {
        return repository.findByUsuarioAndActivaTrueOrderByCodigoAsc(usuario);
    }

    /**
     * Listar cuentas por tipo
     */
    public List<PlanCuentas> listarPorTipo(Usuario usuario, String tipo) {
        return repository.findByUsuarioAndTipoOrderByCodigoAsc(usuario, tipo);
    }

    /**
     * Listar cuentas por nivel
     */
    public List<PlanCuentas> listarPorNivel(Usuario usuario, Integer nivel) {
        return repository.findByUsuarioAndNivelOrderByCodigoAsc(usuario, nivel);
    }

    /**
     * Listar solo cuentas que aceptan movimientos
     */
    public List<PlanCuentas> listarCuentasMovimiento(Usuario usuario) {
        return repository.findByUsuarioAndAceptaMovimientosTrueAndActivaTrueOrderByCodigoAsc(usuario);
    }

    /**
     * Listar cuentas raíz (nivel 1)
     */
    public List<PlanCuentas> listarCuentasRaiz(Usuario usuario) {
        return repository.findByUsuarioAndCuentaPadreIsNullOrderByCodigoAsc(usuario);
    }

    /**
     * Listar subcuentas de una cuenta
     */
    public List<PlanCuentas> listarSubcuentas(PlanCuentas cuentaPadre) {
        return repository.findByCuentaPadreOrderByCodigoAsc(cuentaPadre);
    }

    /**
     * Buscar cuenta por ID
     */
    public Optional<PlanCuentas> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Buscar cuenta por código
     */
    public Optional<PlanCuentas> buscarPorCodigo(String codigo, Usuario usuario) {
        return repository.findByCodigoAndUsuario(codigo, usuario);
    }

    /**
     * Buscar cuentas por nombre (búsqueda parcial)
     */
    public List<PlanCuentas> buscarPorNombre(Usuario usuario, String nombre) {
        return repository.buscarPorNombre(usuario, nombre);
    }

    /**
     * Buscar cuentas por código (búsqueda parcial)
     */
    public List<PlanCuentas> buscarPorCodigoLike(Usuario usuario, String codigo) {
        return repository.buscarPorCodigo(usuario, codigo);
    }

    /**
     * Crear nueva cuenta
     */
    @Transactional
    public PlanCuentas crear(PlanCuentas cuenta) {
        // Validar que no exista el código
        if (repository.existsByCodigoAndUsuario(cuenta.getCodigo(), cuenta.getUsuario())) {
            throw new RuntimeException("Ya existe una cuenta con el código: " + cuenta.getCodigo());
        }

        // Si tiene cuenta padre, validar coherencia de niveles
        if (cuenta.getCuentaPadre() != null) {
            if (cuenta.getNivel() <= cuenta.getCuentaPadre().getNivel()) {
                throw new RuntimeException("El nivel de la subcuenta debe ser mayor al de la cuenta padre");
            }
        }

        return repository.save(cuenta);
    }

    /**
     * Actualizar cuenta existente
     */
    @Transactional
    public PlanCuentas actualizar(Long id, PlanCuentas cuentaActualizada) {
        PlanCuentas cuentaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Actualizar campos
        cuentaExistente.setNombre(cuentaActualizada.getNombre());
        cuentaExistente.setDescripcion(cuentaActualizada.getDescripcion());
        cuentaExistente.setAceptaMovimientos(cuentaActualizada.getAceptaMovimientos());
        cuentaExistente.setActiva(cuentaActualizada.getActiva());

        return repository.save(cuentaExistente);
    }

    /**
     * Activar/Desactivar cuenta
     */
    @Transactional
    public PlanCuentas cambiarEstado(Long id, Boolean activa) {
        PlanCuentas cuenta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setActiva(activa);
        return repository.save(cuenta);
    }

    /**
     * Eliminar cuenta (solo si no tiene subcuentas ni movimientos)
     */
    @Transactional
    public void eliminar(Long id) {
        PlanCuentas cuenta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Verificar que no tenga subcuentas
        List<PlanCuentas> subcuentas = repository.findByCuentaPadreOrderByCodigoAsc(cuenta);
        if (!subcuentas.isEmpty()) {
            throw new RuntimeException("No se puede eliminar una cuenta con subcuentas");
        }

        // TODO: Verificar que no tenga movimientos contables asociados

        repository.delete(cuenta);
    }

    /**
     * Crear plan de cuentas inicial (PUC Colombia)
     */
    @Transactional
    public void crearPlanInicial(Usuario usuario) {
        // Verificar si ya tiene cuentas
        List<PlanCuentas> cuentasExistentes = repository.findByUsuarioOrderByCodigoAsc(usuario);
        if (!cuentasExistentes.isEmpty()) {
            throw new RuntimeException("El usuario ya tiene un plan de cuentas configurado");
        }

        // Crear cuentas de Nivel 1 (Clases principales según PUC Colombia)
        crearCuenta("1", "ACTIVO", "ACTIVO", 1, "DEBITO", false, usuario, null);
        crearCuenta("2", "PASIVO", "PASIVO", 1, "CREDITO", false, usuario, null);
        crearCuenta("3", "PATRIMONIO", "PATRIMONIO", 1, "CREDITO", false, usuario, null);
        crearCuenta("4", "INGRESOS", "INGRESO", 1, "CREDITO", false, usuario, null);
        crearCuenta("5", "GASTOS", "GASTO", 1, "DEBITO", false, usuario, null);
        crearCuenta("6", "COSTOS DE VENTAS", "COSTO", 1, "DEBITO", false, usuario, null);
    }

    /**
     * Helper para crear cuenta
     */
    private PlanCuentas crearCuenta(String codigo, String nombre, String tipo, Integer nivel,
                                     String naturaleza, Boolean aceptaMovimientos,
                                     Usuario usuario, PlanCuentas cuentaPadre) {
        PlanCuentas cuenta = new PlanCuentas();
        cuenta.setCodigo(codigo);
        cuenta.setNombre(nombre);
        cuenta.setTipo(tipo);
        cuenta.setNivel(nivel);
        cuenta.setNaturaleza(naturaleza);
        cuenta.setAceptaMovimientos(aceptaMovimientos);
        cuenta.setActiva(true);
        cuenta.setUsuario(usuario);
        cuenta.setCuentaPadre(cuentaPadre);

        return repository.save(cuenta);
    }
}
