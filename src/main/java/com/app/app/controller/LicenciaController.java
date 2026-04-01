package com.app.app.controller;

import com.app.app.model.LicenciaPrueba;
import com.app.app.service.LicenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/licencia")
public class LicenciaController {

    @Autowired
    private LicenciaService licenciaService;

    /**
     * Endpoint para iniciar la prueba de 7 días
     */
    @PostMapping("/iniciar-prueba")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> iniciarPrueba() {
        Map<String, Object> response = new HashMap<>();

        try {
            LicenciaPrueba licencia = licenciaService.registrarLicenciaPrueba();

            response.put("success", true);
            response.put("mensaje", "Prueba de 7 días iniciada exitosamente");
            response.put("diasRestantes", licencia.getDiasRestantes());
            response.put("fechaExpiracion", licencia.getFechaExpiracion());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para verificar el estado de la licencia
     */
    @GetMapping("/estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarEstado() {
        Map<String, Object> response = new HashMap<>();

        try {
            LicenciaService.EstadoLicencia estado = licenciaService.verificarEstadoLicencia();

            response.put("valida", estado.isValida());
            response.put("mensaje", estado.getMensaje());
            response.put("diasRestantes", estado.getDiasRestantes());
            response.put("fechaExpiracion", estado.getFechaExpiracion());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("valida", false);
            response.put("mensaje", "Error al verificar el estado: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Endpoint para obtener información de la licencia actual
     */
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            String macAddress = licenciaService.obtenerMacAddress();
            Optional<LicenciaPrueba> licenciaOpt = licenciaService.obtenerLicenciaActual();

            response.put("macAddress", macAddress);

            if (licenciaOpt.isPresent()) {
                LicenciaPrueba licencia = licenciaOpt.get();
                response.put("tieneRegistro", true);
                response.put("activa", licencia.getActiva());
                response.put("bloqueada", licencia.getBloqueada());
                response.put("fechaInicio", licencia.getFechaInicio());
                response.put("fechaExpiracion", licencia.getFechaExpiracion());
                response.put("diasRestantes", licencia.getDiasRestantes());
                response.put("expirada", licencia.haExpirado());
            } else {
                response.put("tieneRegistro", false);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Página de información de la licencia
     */
    @GetMapping("/info-page")
    public String infoPage(Model model) {
        try {
            LicenciaService.EstadoLicencia estado = licenciaService.verificarEstadoLicencia();
            model.addAttribute("estado", estado);

            Optional<LicenciaPrueba> licenciaOpt = licenciaService.obtenerLicenciaActual();
            licenciaOpt.ifPresent(licencia -> model.addAttribute("licencia", licencia));

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "licencia-info";
    }
}
