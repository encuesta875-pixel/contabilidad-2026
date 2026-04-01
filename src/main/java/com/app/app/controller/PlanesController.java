package com.app.app.controller;

import com.app.app.model.LicenciaPrueba;
import com.app.app.model.Usuario;
import com.app.app.service.LicenciaService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/planes")
public class PlanesController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LicenciaService licenciaService;

    @GetMapping
    public String mostrarPlanes(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) String expired) {

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email).orElse(null);

            if (usuario != null) {
                model.addAttribute("usuario", usuario);

                // Verificar si hay suscripción activa
                if (usuario.getSuscripcion() != null) {
                    model.addAttribute("tieneSuscripcion", usuario.getSuscripcion().getActiva());
                } else {
                    model.addAttribute("tieneSuscripcion", false);
                }
            }
        }

        // Verificar estado de la licencia de prueba
        try {
            Optional<LicenciaPrueba> licenciaOpt = licenciaService.obtenerLicenciaActual();

            if (licenciaOpt.isPresent()) {
                LicenciaPrueba licencia = licenciaOpt.get();
                model.addAttribute("tieneLicenciaPrueba", true);
                model.addAttribute("licenciaActiva", licencia.getActiva() && !licencia.haExpirado());
                model.addAttribute("diasRestantes", licencia.getDiasRestantes());
                model.addAttribute("licenciaExpirada", licencia.haExpirado());
            } else {
                model.addAttribute("tieneLicenciaPrueba", false);
                model.addAttribute("puedeIniciarPrueba", true);
            }
        } catch (Exception e) {
            model.addAttribute("errorLicencia", e.getMessage());
        }

        // Indicar si viene por expiración
        if ("true".equals(expired)) {
            model.addAttribute("mensajeExpiracion", "Tu período de prueba ha expirado. Por favor, selecciona un plan para continuar usando la aplicación.");
        }

        model.addAttribute("currentPage", "planes");
        return "planes";
    }
}
