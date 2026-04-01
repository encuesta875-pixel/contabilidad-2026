package com.app.app.controller;

import com.app.app.model.Usuario;
import com.app.app.service.ChatService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String chatView(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("currentPage", "chat");
        model.addAttribute("usuario", usuario);
        return "chat";
    }

    @PostMapping("/mensaje")
    public ResponseEntity<Map<String, String>> procesarMensaje(@RequestBody Map<String, String> request,
                                                                Authentication authentication) {
        String mensaje = request.get("mensaje");
        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String respuesta = chatService.procesarMensaje(mensaje, usuario);

        return ResponseEntity.ok(Map.of("respuesta", respuesta));
    }
}
