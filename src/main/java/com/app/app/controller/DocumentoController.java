package com.app.app.controller;

import com.app.app.model.Documento;
import com.app.app.model.Usuario;
import com.app.app.service.DocumentoService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarDocumentos(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Documento> documentos = documentoService.listarPorUsuario(usuario);

        model.addAttribute("currentPage", "documentos");
        model.addAttribute("usuario", usuario);
        model.addAttribute("documentos", documentos);

        return "documentos";
    }

    @PostMapping("/subir")
    public String subirDocumento(@RequestParam("file") MultipartFile file,
                                Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            documentoService.subirDocumento(file, usuario);
            return "redirect:/documentos?success=true";
        } catch (IOException e) {
            return "redirect:/documentos?error=true";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDocumento(@PathVariable Long id) {
        try {
            documentoService.eliminarDocumento(id);
            return "redirect:/documentos?deleted=true";
        } catch (IOException e) {
            return "redirect:/documentos?error=true";
        }
    }
}
