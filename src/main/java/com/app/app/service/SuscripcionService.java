package com.app.app.service;

import com.app.app.model.Suscripcion;
import com.app.app.model.Usuario;
import com.app.app.repository.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SuscripcionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private LicenciaService licenciaService;

    /**
     * Crea o actualiza una suscripción de pago para un usuario
     * IMPORTANTE: Este método PERMITE crear suscripciones incluso si la licencia de prueba ha expirado
     * El bloqueo de licencia de prueba NO afecta la capacidad de adquirir suscripciones de pago
     */
    @Transactional
    public Suscripcion crearOActualizarSuscripcion(Usuario usuario, String tipoPlan, BigDecimal precio) {
        // Obtener MAC address del dispositivo
        String macAddress = licenciaService.obtenerMacAddress();

        // Buscar si ya existe una suscripción para este usuario
        Optional<Suscripcion> suscripcionExistente = suscripcionRepository.findByUsuario(usuario);

        Suscripcion suscripcion;

        if (suscripcionExistente.isPresent()) {
            // Actualizar suscripción existente
            suscripcion = suscripcionExistente.get();
            suscripcion.setTipoPlan(tipoPlan);
            suscripcion.setPrecio(precio);
            suscripcion.setFechaInicio(LocalDateTime.now());
            suscripcion.setFechaFin(calcularFechaFin(tipoPlan));
            suscripcion.setActiva(true);
            suscripcion.setMacAddress(macAddress);
        } else {
            // Crear nueva suscripción
            suscripcion = new Suscripcion();
            suscripcion.setUsuario(usuario);
            suscripcion.setTipoPlan(tipoPlan);
            suscripcion.setPrecio(precio);
            suscripcion.setFechaInicio(LocalDateTime.now());
            suscripcion.setFechaFin(calcularFechaFin(tipoPlan));
            suscripcion.setActiva(true);
            suscripcion.setMacAddress(macAddress);
        }

        return suscripcionRepository.save(suscripcion);
    }

    /**
     * Calcula la fecha de fin según el tipo de plan
     */
    private LocalDateTime calcularFechaFin(String tipoPlan) {
        LocalDateTime ahora = LocalDateTime.now();

        switch (tipoPlan.toUpperCase()) {
            case "MENSUAL":
                return ahora.plusMonths(1);
            case "TRIMESTRAL":
                return ahora.plusMonths(3);
            case "SEMESTRAL":
                return ahora.plusMonths(6);
            case "ANUAL":
                return ahora.plusYears(1);
            default:
                // Por defecto, 1 mes
                return ahora.plusMonths(1);
        }
    }

    /**
     * Obtiene la suscripción activa de un usuario
     */
    @Transactional(readOnly = true)
    public Optional<Suscripcion> obtenerSuscripcionActiva(Usuario usuario) {
        return suscripcionRepository.findByUsuarioAndActivaTrue(usuario);
    }

    /**
     * Verifica si un usuario tiene una suscripción activa y no expirada
     */
    @Transactional(readOnly = true)
    public boolean tieneSuscripcionValida(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = suscripcionRepository.findByUsuarioAndActivaTrue(usuario);

        if (suscripcionOpt.isEmpty()) {
            return false;
        }

        Suscripcion suscripcion = suscripcionOpt.get();

        // Verificar si ha expirado
        if (suscripcion.getFechaFin() != null && LocalDateTime.now().isAfter(suscripcion.getFechaFin())) {
            // Desactivar la suscripción si ha expirado
            suscripcion.setActiva(false);
            suscripcionRepository.save(suscripcion);
            return false;
        }

        return true;
    }

    /**
     * Cancela la suscripción de un usuario
     */
    @Transactional
    public void cancelarSuscripcion(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = suscripcionRepository.findByUsuario(usuario);

        if (suscripcionOpt.isPresent()) {
            Suscripcion suscripcion = suscripcionOpt.get();
            suscripcion.setActiva(false);
            suscripcionRepository.save(suscripcion);
        }
    }

    /**
     * Renueva una suscripción existente
     */
    @Transactional
    public Suscripcion renovarSuscripcion(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = suscripcionRepository.findByUsuario(usuario);

        if (suscripcionOpt.isEmpty()) {
            throw new RuntimeException("No existe una suscripción para renovar");
        }

        Suscripcion suscripcion = suscripcionOpt.get();

        // Calcular nueva fecha de inicio desde la fecha de fin anterior o ahora
        LocalDateTime nuevaFechaInicio = suscripcion.getFechaFin() != null &&
                                         LocalDateTime.now().isBefore(suscripcion.getFechaFin())
                ? suscripcion.getFechaFin()
                : LocalDateTime.now();

        suscripcion.setFechaInicio(nuevaFechaInicio);
        suscripcion.setFechaFin(calcularFechaFinDesde(suscripcion.getTipoPlan(), nuevaFechaInicio));
        suscripcion.setActiva(true);

        return suscripcionRepository.save(suscripcion);
    }

    /**
     * Calcula la fecha de fin desde una fecha específica
     */
    private LocalDateTime calcularFechaFinDesde(String tipoPlan, LocalDateTime fechaInicio) {
        switch (tipoPlan.toUpperCase()) {
            case "MENSUAL":
                return fechaInicio.plusMonths(1);
            case "TRIMESTRAL":
                return fechaInicio.plusMonths(3);
            case "SEMESTRAL":
                return fechaInicio.plusMonths(6);
            case "ANUAL":
                return fechaInicio.plusYears(1);
            default:
                return fechaInicio.plusMonths(1);
        }
    }

    /**
     * Obtiene los días restantes de una suscripción
     */
    @Transactional(readOnly = true)
    public long obtenerDiasRestantes(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = obtenerSuscripcionActiva(usuario);

        if (suscripcionOpt.isEmpty()) {
            return 0;
        }

        Suscripcion suscripcion = suscripcionOpt.get();

        if (suscripcion.getFechaFin() == null) {
            return 0;
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(suscripcion.getFechaFin())) {
            return 0;
        }

        return java.time.Duration.between(ahora, suscripcion.getFechaFin()).toDays();
    }
}
