package com.app.app.controller;

import com.app.app.model.Usuario;
import com.app.app.service.TransaccionService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, java.math.BigDecimal> resumen = transaccionService.calcularResumen(usuario);

        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalIngresos", resumen.get("totalIngresos"));
        model.addAttribute("totalGastos", resumen.get("totalGastos"));
        model.addAttribute("balance", resumen.get("balance"));
        model.addAttribute("ultimasTransacciones", transaccionService.obtenerUltimasTransacciones(usuario, 5));

        return "dashboard";
    }
}
