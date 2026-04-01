package com.app.app.controller;

import com.app.app.model.AsientoContable;
import com.app.app.model.DetalleAsiento;
import com.app.app.model.PlanCuentas;
import com.app.app.model.Usuario;
import com.app.app.service.AsientoContableService;
import com.app.app.service.PlanCuentasService;
import com.app.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/asientos")
public class AsientoContableController {

    @Autowired
    private AsientoContableService asientoService;

    @Autowired
    private PlanCuentasService planCuentasService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Authentication authentication, Model model,
                        @RequestParam(required = false) String estado) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        List<AsientoContable> asientos;
        if (estado != null && !estado.isEmpty()) {
            asientos = asientoService.listarPorEstado(usuario, estado);
        } else {
            asientos = asientoService.listarPorUsuario(usuario);
        }

        // Obtener cuentas para el selector
        List<PlanCuentas> cuentasMovimiento = planCuentasService.listarCuentasMovimiento(usuario);

        model.addAttribute("currentPage", "asientos");
        model.addAttribute("usuario", usuario);
        model.addAttribute("asientos", asientos);
        model.addAttribute("cuentas", cuentasMovimiento);
        model.addAttribute("filtroEstado", estado);

        // Estadísticas
        model.addAttribute("totalBorrador", asientoService.contarPorEstado(usuario, "BORRADOR"));
        model.addAttribute("totalContabilizado", asientoService.contarPorEstado(usuario, "CONTABILIZADO"));

        return "asientos";
    }

    @GetMapping("/nuevo")
    public String nuevoFormulario(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        AsientoContable asiento = new AsientoContable();
        asiento.setUsuario(usuario);
        asiento.setFecha(LocalDate.now());
        asiento.setNumeroAsiento(asientoService.obtenerSiguienteNumero(usuario));

        List<PlanCuentas> cuentasMovimiento = planCuentasService.listarCuentasMovimiento(usuario);

        model.addAttribute("currentPage", "asientos");
        model.addAttribute("usuario", usuario);
        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentasMovimiento);

        return "asiento-form";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam Long numeroAsiento,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                       @RequestParam String concepto,
                       @RequestParam(required = false) String documentoReferencia,
                       @RequestParam(required = false) List<Long> cuentaId,
                       @RequestParam(required = false) List<BigDecimal> debito,
                       @RequestParam(required = false) List<BigDecimal> credito,
                       @RequestParam(required = false) List<String> descripcionDetalle,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);

            // Crear asiento
            AsientoContable asiento = new AsientoContable();
            asiento.setUsuario(usuario);
            asiento.setNumeroAsiento(numeroAsiento);
            asiento.setFecha(fecha);
            asiento.setConcepto(concepto);
            asiento.setDocumentoReferencia(documentoReferencia);

            // Crear detalles si existen
            if (cuentaId != null && !cuentaId.isEmpty()) {
                for (int i = 0; i < cuentaId.size(); i++) {
                    PlanCuentas cuenta = planCuentasService.buscarPorId(cuentaId.get(i))
                            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

                    DetalleAsiento detalle = new DetalleAsiento();
                    detalle.setCuenta(cuenta);
                    detalle.setDebito(debito.get(i) != null ? debito.get(i) : BigDecimal.ZERO);
                    detalle.setCredito(credito.get(i) != null ? credito.get(i) : BigDecimal.ZERO);
                    detalle.setDescripcion(descripcionDetalle != null && i < descripcionDetalle.size() ? descripcionDetalle.get(i) : "");
                    detalle.setOrden(i + 1);

                    asiento.agregarDetalle(detalle);
                }
            }

            asientoService.crear(asiento);
            redirectAttributes.addFlashAttribute("success", "Asiento creado exitosamente");
            return "redirect:/asientos";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/asientos/nuevo";
        }
    }

    @PostMapping("/contabilizar/{id}")
    public String contabilizar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.contabilizar(id);
            redirectAttributes.addFlashAttribute("success", "Asiento contabilizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asientos";
    }

    @PostMapping("/anular/{id}")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.anular(id);
            redirectAttributes.addFlashAttribute("success", "Asiento anulado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asientos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            asientoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Asiento eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asientos";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        AsientoContable asiento = asientoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        model.addAttribute("currentPage", "asientos");
        model.addAttribute("usuario", usuario);
        model.addAttribute("asiento", asiento);

        return "asiento-detalle";
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
