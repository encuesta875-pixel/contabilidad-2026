package com.app.app.service;

import com.app.app.model.LicenciaPrueba;
import com.app.app.repository.LicenciaPruebaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LicenciaService {

    @Autowired
    private LicenciaPruebaRepository licenciaPruebaRepository;

    /**
     * Obtiene la dirección MAC del dispositivo actual
     * Mejorado para funcionar en Windows, Linux y Mac
     */
    public String obtenerMacAddress() {
        try {
            // Intentar obtener la interfaz de red principal
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(localHost);

            // Si no funciona con localhost, buscar la primera interfaz válida
            if (network == null || network.getHardwareAddress() == null) {
                java.util.Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface ni = networkInterfaces.nextElement();
                    byte[] mac = ni.getHardwareAddress();

                    // Buscar una interfaz activa con MAC válida
                    if (mac != null && mac.length == 6 && !ni.isLoopback() && !ni.isVirtual() && ni.isUp()) {
                        network = ni;
                        break;
                    }
                }
            }

            if (network == null) {
                // Si no se encuentra interfaz de red, usar un identificador único del sistema
                System.err.println("ADVERTENCIA: No se pudo obtener MAC address, usando identificador alternativo");
                return "SYSTEM-" + System.getProperty("user.name") + "-" +
                       System.getProperty("os.name").replaceAll("\\s", "");
            }

            byte[] macBytes = network.getHardwareAddress();
            if (macBytes == null) {
                // Usar identificador alternativo
                System.err.println("ADVERTENCIA: MAC address es null, usando identificador alternativo");
                return "SYSTEM-" + System.getProperty("user.name") + "-" +
                       System.getProperty("os.name").replaceAll("\\s", "");
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < macBytes.length; i++) {
                sb.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? "-" : ""));
            }
            return sb.toString();

        } catch (UnknownHostException | SocketException e) {
            // Si todo falla, usar un identificador único basado en el sistema
            System.err.println("ERROR al obtener MAC address: " + e.getMessage());
            return "SYSTEM-" + System.getProperty("user.name") + "-" +
                   System.getProperty("os.name").replaceAll("\\s", "");
        }
    }

    /**
     * Verifica si existe una licencia de prueba válida para este dispositivo
     */
    @Transactional(readOnly = true)
    public boolean tieneLicenciaValida() {
        String macAddress = obtenerMacAddress();
        Optional<LicenciaPrueba> licenciaOpt = licenciaPruebaRepository.findByMacAddress(macAddress);

        if (licenciaOpt.isEmpty()) {
            return false;
        }

        LicenciaPrueba licencia = licenciaOpt.get();

        // Verificar si está bloqueada
        if (licencia.getBloqueada()) {
            return false;
        }

        // Verificar si ha expirado
        if (licencia.haExpirado()) {
            // Desactivar la licencia si ha expirado
            licencia.setActiva(false);
            licenciaPruebaRepository.save(licencia);
            return false;
        }

        return licencia.getActiva();
    }

    /**
     * Registra una nueva licencia de prueba para este dispositivo
     */
    @Transactional
    public LicenciaPrueba registrarLicenciaPrueba() {
        String macAddress = obtenerMacAddress();

        // Verificar si ya existe una licencia para esta MAC
        Optional<LicenciaPrueba> licenciaExistente = licenciaPruebaRepository.findByMacAddress(macAddress);

        if (licenciaExistente.isPresent()) {
            LicenciaPrueba licencia = licenciaExistente.get();

            // Si la licencia existe pero ha expirado, no permitir crear OTRA PRUEBA GRATUITA
            // El usuario PUEDE y DEBE adquirir una suscripción de pago
            if (licencia.haExpirado()) {
                licencia.setBloqueada(true);
                licencia.setMotivoBloqueo("Período de prueba expirado. Adquiere una suscripción para continuar.");
                licenciaPruebaRepository.save(licencia);
                throw new RuntimeException("El período de prueba ha expirado. Por favor, adquiere una suscripción para continuar usando la aplicación.");
            }

            return licencia;
        }

        // Crear nueva licencia de prueba
        LicenciaPrueba nuevaLicencia = new LicenciaPrueba(macAddress);
        return licenciaPruebaRepository.save(nuevaLicencia);
    }

    /**
     * Obtiene información de la licencia actual del dispositivo
     */
    @Transactional(readOnly = true)
    public Optional<LicenciaPrueba> obtenerLicenciaActual() {
        String macAddress = obtenerMacAddress();
        return licenciaPruebaRepository.findByMacAddress(macAddress);
    }

    /**
     * Verifica el estado de la licencia y retorna información detallada
     */
    @Transactional(readOnly = true)
    public EstadoLicencia verificarEstadoLicencia() {
        try {
            String macAddress = obtenerMacAddress();
            Optional<LicenciaPrueba> licenciaOpt = licenciaPruebaRepository.findByMacAddress(macAddress);

            if (licenciaOpt.isEmpty()) {
                return new EstadoLicencia(false, "No hay licencia registrada", 0, null);
            }

            LicenciaPrueba licencia = licenciaOpt.get();

            if (licencia.getBloqueada()) {
                return new EstadoLicencia(false, "Licencia bloqueada: " + licencia.getMotivoBloqueo(), 0, licencia.getFechaExpiracion());
            }

            if (licencia.haExpirado()) {
                return new EstadoLicencia(false, "Licencia expirada", 0, licencia.getFechaExpiracion());
            }

            long diasRestantes = licencia.getDiasRestantes();
            String mensaje = diasRestantes == 1
                ? "Queda 1 día de prueba"
                : "Quedan " + diasRestantes + " días de prueba";

            return new EstadoLicencia(true, mensaje, diasRestantes, licencia.getFechaExpiracion());

        } catch (Exception e) {
            return new EstadoLicencia(false, "Error al verificar licencia: " + e.getMessage(), 0, null);
        }
    }

    /**
     * Clase interna para retornar el estado de la licencia
     */
    public static class EstadoLicencia {
        private final boolean valida;
        private final String mensaje;
        private final long diasRestantes;
        private final LocalDateTime fechaExpiracion;

        public EstadoLicencia(boolean valida, String mensaje, long diasRestantes, LocalDateTime fechaExpiracion) {
            this.valida = valida;
            this.mensaje = mensaje;
            this.diasRestantes = diasRestantes;
            this.fechaExpiracion = fechaExpiracion;
        }

        public boolean isValida() {
            return valida;
        }

        public String getMensaje() {
            return mensaje;
        }

        public long getDiasRestantes() {
            return diasRestantes;
        }

        public LocalDateTime getFechaExpiracion() {
            return fechaExpiracion;
        }
    }
}
