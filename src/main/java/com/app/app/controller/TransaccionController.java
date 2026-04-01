package com.app.app.controller;

import com.app.app.model.Transaccion;
import com.app.app.model.Usuario;
import com.app.app.service.TransaccionService;
import com.app.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarTransacciones(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Transaccion> transacciones = transaccionService.listarPorUsuario(usuario);

        model.addAttribute("currentPage", "transacciones");
        model.addAttribute("usuario", usuario);
        model.addAttribute("transacciones", transacciones);
        model.addAttribute("transaccion", new Transaccion());

        return "transacciones";
    }

    @PostMapping("/crear")
    public String crearTransaccion(@Valid @ModelAttribute("transaccion") Transaccion transaccion,
                                  BindingResult result,
                                  Authentication authentication,
                                  Model model) {
        if (result.hasErrors()) {
            return "redirect:/transacciones?error=true";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        transaccion.setUsuario(usuario);
        transaccionService.crearTransaccion(transaccion);

        return "redirect:/transacciones?success=true";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return "redirect:/transacciones?deleted=true";
    }
}
