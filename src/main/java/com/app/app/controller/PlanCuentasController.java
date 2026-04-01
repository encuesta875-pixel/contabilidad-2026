package com.app.app.controller;

import com.app.app.model.PlanCuentas;
import com.app.app.model.Usuario;
import com.app.app.service.PlanCuentasService;
import com.app.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/plan-cuentas")
public class PlanCuentasController {

    @Autowired
    private PlanCuentasService planCuentasService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Authentication authentication, Model model,
                        @RequestParam(required = false) String tipo,
                        @RequestParam(required = false) Integer nivel) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<PlanCuentas> cuentas;
        if (tipo != null && !tipo.isEmpty()) {
            cuentas = planCuentasService.listarPorTipo(usuario, tipo);
        } else if (nivel != null) {
            cuentas = planCuentasService.listarPorNivel(usuario, nivel);
        } else {
            cuentas = planCuentasService.listarPorUsuario(usuario);
        }

        model.addAttribute("currentPage", "plan-cuentas");
        model.addAttribute("usuario", usuario);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("planCuenta", new PlanCuentas());
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroNivel", nivel);

        return "plan-cuentas";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("planCuenta") PlanCuentas cuenta,
                       BindingResult result,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error en los datos del formulario");
            return "redirect:/plan-cuentas";
        }

        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            cuenta.setUsuario(usuario);

            // Si se especificó cuenta padre, buscarla
            if (cuenta.getCuentaPadre() != null && cuenta.getCuentaPadre().getId() != null) {
                PlanCuentas cuentaPadre = planCuentasService.buscarPorId(cuenta.getCuentaPadre().getId())
                        .orElseThrow(() -> new RuntimeException("Cuenta padre no encontrada"));
                cuenta.setCuentaPadre(cuentaPadre);
            } else {
                cuenta.setCuentaPadre(null);
            }

            planCuentasService.crear(cuenta);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/plan-cuentas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            planCuentasService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cuenta eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/plan-cuentas";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam Boolean activa,
                               RedirectAttributes redirectAttributes) {
        try {
            planCuentasService.cambiarEstado(id, activa);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/plan-cuentas";
    }

    @PostMapping("/inicializar")
    public String inicializar(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            planCuentasService.crearPlanInicial(usuario);
            redirectAttributes.addFlashAttribute("success", "Plan de cuentas inicial creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/plan-cuentas";
    }

    @GetMapping("/buscar")
    public String buscar(Authentication authentication,
                        @RequestParam(required = false) String query,
                        Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<PlanCuentas> cuentas;
        if (query != null && !query.isEmpty()) {
            // Intentar buscar por código o nombre
            if (query.matches("\\d+")) {
                cuentas = planCuentasService.buscarPorCodigoLike(usuario, query);
            } else {
                cuentas = planCuentasService.buscarPorNombre(usuario, query);
            }
        } else {
            cuentas = planCuentasService.listarPorUsuario(usuario);
        }

        model.addAttribute("currentPage", "plan-cuentas");
        model.addAttribute("usuario", usuario);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("planCuenta", new PlanCuentas());
        model.addAttribute("query", query);

        return "plan-cuentas";
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
