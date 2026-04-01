package com.app.app.controller;

import com.app.app.model.Suscripcion;
import com.app.app.model.Usuario;
import com.app.app.service.SuscripcionService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/suscripcion")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para crear o actualizar una suscripción de pago
     * IMPORTANTE: Este endpoint PERMITE crear suscripciones incluso si la licencia de prueba ha expirado
     */
    @PostMapping("/adquirir")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adquirirSuscripcion(
            Authentication authentication,
            @RequestParam String tipoPlan,
            @RequestParam BigDecimal precio) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear o actualizar suscripción
            // Este método NO verifica ni bloquea por licencia de prueba expirada
            Suscripcion suscripcion = suscripcionService.crearOActualizarSuscripcion(usuario, tipoPlan, precio);

            response.put("success", true);
            response.put("mensaje", "Suscripción adquirida exitosamente");
            response.put("tipoPlan", suscripcion.getTipoPlan());
            response.put("fechaInicio", suscripcion.getFechaInicio());
            response.put("fechaFin", suscripcion.getFechaFin());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para renovar una suscripción existente
     */
    @PostMapping("/renovar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> renovarSuscripcion(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Suscripcion suscripcion = suscripcionService.renovarSuscripcion(usuario);

            response.put("success", true);
            response.put("mensaje", "Suscripción renovada exitosamente");
            response.put("fechaFin", suscripcion.getFechaFin());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para cancelar una suscripción
     */
    @PostMapping("/cancelar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelarSuscripcion(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            suscripcionService.cancelarSuscripcion(usuario);

            response.put("success", true);
            response.put("mensaje", "Suscripción cancelada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para verificar el estado de la suscripción
     */
    @GetMapping("/estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarEstado(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            boolean tieneValida = suscripcionService.tieneSuscripcionValida(usuario);
            response.put("tieneSuscripcionValida", tieneValida);

            if (tieneValida) {
                long diasRestantes = suscripcionService.obtenerDiasRestantes(usuario);
                response.put("diasRestantes", diasRestantes);
                response.put("mensaje", "Suscripción activa con " + diasRestantes + " días restantes");
            } else {
                response.put("mensaje", "No tienes una suscripción activa");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
