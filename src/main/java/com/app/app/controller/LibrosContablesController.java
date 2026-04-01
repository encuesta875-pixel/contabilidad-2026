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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LibrosContablesController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AsientoContableService asientoService;

    @Autowired
    private PlanCuentasService planCuentasService;

    @GetMapping("/libro-diario")
    public String libroDiario(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        // Obtener asientos contabilizados del periodo
        List<AsientoContable> asientos = asientoService.obtenerLibroDiario(usuario, fechaInicio, fechaFin);

        // Calcular totales
        BigDecimal totalDebitos = asientos.stream()
                .map(AsientoContable::getTotalDebito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditos = asientos.stream()
                .map(AsientoContable::getTotalCredito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("asientos", asientos);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("currentPage", "libro-diario");
        model.addAttribute("usuario", usuario);

        return "libro-diario";
    }

    @GetMapping("/libro-mayor")
    public String libroMayor(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) Long cuentaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        // Obtener todas las cuentas activas del usuario para el selector
        List<PlanCuentas> cuentasDisponibles = planCuentasService.listarActivasPorUsuario(usuario);

        // Si se seleccionó una cuenta, obtener sus movimientos
        List<DetalleAsiento> movimientos = new ArrayList<>();
        PlanCuentas cuentaSeleccionada = null;
        BigDecimal saldoInicial = BigDecimal.ZERO;
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;
        BigDecimal saldoFinal = BigDecimal.ZERO;

        if (cuentaId != null) {
            cuentaSeleccionada = planCuentasService.buscarPorId(cuentaId)
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

            // Obtener movimientos de la cuenta en el periodo
            movimientos = asientoService.obtenerLibroMayorCuenta(cuentaSeleccionada, fechaInicio, fechaFin);

            // Calcular totales
            totalDebitos = movimientos.stream()
                    .map(DetalleAsiento::getDebito)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalCreditos = movimientos.stream()
                    .map(DetalleAsiento::getCredito)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular saldo según naturaleza de la cuenta
            if ("DEBITO".equals(cuentaSeleccionada.getNaturaleza())) {
                saldoFinal = totalDebitos.subtract(totalCreditos);
            } else {
                saldoFinal = totalCreditos.subtract(totalDebitos);
            }
        }

        model.addAttribute("cuentasDisponibles", cuentasDisponibles);
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("saldoInicial", saldoInicial);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("saldoFinal", saldoFinal);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("currentPage", "libro-mayor");
        model.addAttribute("usuario", usuario);

        return "libro-mayor";
    }

    @GetMapping("/balance-general")
    public String balanceGeneral(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        // Si no se especifica fecha, usar hoy
        if (fechaCorte == null) {
            fechaCorte = LocalDate.now();
        }

        // Obtener cuentas por tipo
        List<PlanCuentas> cuentasActivo = planCuentasService.listarPorTipo(usuario, "ACTIVO");
        List<PlanCuentas> cuentasPasivo = planCuentasService.listarPorTipo(usuario, "PASIVO");
        List<PlanCuentas> cuentasPatrimonio = planCuentasService.listarPorTipo(usuario, "PATRIMONIO");

        // Calcular saldos
        Map<Long, BigDecimal> saldos = new HashMap<>();
        BigDecimal totalActivo = BigDecimal.ZERO;
        BigDecimal totalPasivo = BigDecimal.ZERO;
        BigDecimal totalPatrimonio = BigDecimal.ZERO;

        // Calcular saldos de activos
        for (PlanCuentas cuenta : cuentasActivo) {
            BigDecimal saldo = asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte);
            saldos.put(cuenta.getId(), saldo);
            totalActivo = totalActivo.add(saldo);
        }

        // Calcular saldos de pasivos
        for (PlanCuentas cuenta : cuentasPasivo) {
            BigDecimal saldo = asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte);
            saldos.put(cuenta.getId(), saldo);
            totalPasivo = totalPasivo.add(saldo);
        }

        // Calcular saldos de patrimonio
        for (PlanCuentas cuenta : cuentasPatrimonio) {
            BigDecimal saldo = asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte);
            saldos.put(cuenta.getId(), saldo);
            totalPatrimonio = totalPatrimonio.add(saldo);
        }

        // Calcular resultado del ejercicio (utilidad o pérdida)
        List<PlanCuentas> cuentasIngreso = planCuentasService.listarPorTipo(usuario, "INGRESO");
        List<PlanCuentas> cuentasGasto = planCuentasService.listarPorTipo(usuario, "GASTO");
        List<PlanCuentas> cuentasCosto = planCuentasService.listarPorTipo(usuario, "COSTO");

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;
        BigDecimal totalCostos = BigDecimal.ZERO;

        for (PlanCuentas cuenta : cuentasIngreso) {
            totalIngresos = totalIngresos.add(asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte));
        }
        for (PlanCuentas cuenta : cuentasGasto) {
            totalGastos = totalGastos.add(asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte));
        }
        for (PlanCuentas cuenta : cuentasCosto) {
            totalCostos = totalCostos.add(asientoService.calcularSaldoCuentaHastaFecha(cuenta, fechaCorte));
        }

        BigDecimal resultadoEjercicio = totalIngresos.subtract(totalGastos).subtract(totalCostos);

        // Verificar ecuación contable
        BigDecimal totalPasivoMasPatrimonio = totalPasivo.add(totalPatrimonio).add(resultadoEjercicio);
        boolean ecuacionCuadrada = totalActivo.compareTo(totalPasivoMasPatrimonio) == 0;

        model.addAttribute("cuentasActivo", cuentasActivo);
        model.addAttribute("cuentasPasivo", cuentasPasivo);
        model.addAttribute("cuentasPatrimonio", cuentasPatrimonio);
        model.addAttribute("saldos", saldos);
        model.addAttribute("totalActivo", totalActivo);
        model.addAttribute("totalPasivo", totalPasivo);
        model.addAttribute("totalPatrimonio", totalPatrimonio);
        model.addAttribute("resultadoEjercicio", resultadoEjercicio);
        model.addAttribute("totalPasivoMasPatrimonio", totalPasivoMasPatrimonio);
        model.addAttribute("ecuacionCuadrada", ecuacionCuadrada);
        model.addAttribute("fechaCorte", fechaCorte);
        model.addAttribute("currentPage", "balance-general");
        model.addAttribute("usuario", usuario);

        return "balance-general";
    }

    @GetMapping("/estado-resultados")
    public String estadoResultados(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "estado-resultados");
        model.addAttribute("usuario", usuario);
        return "estado-resultados";
    }

    @GetMapping("/flujo-efectivo")
    public String flujoEfectivo(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "flujo-efectivo");
        model.addAttribute("usuario", usuario);
        return "flujo-efectivo";
    }

    @GetMapping("/bancos")
    public String bancos(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "bancos");
        model.addAttribute("usuario", usuario);
        return "bancos";
    }

    @GetMapping("/conciliacion")
    public String conciliacion(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "conciliacion");
        model.addAttribute("usuario", usuario);
        return "conciliacion";
    }

    @GetMapping("/cuentas-cobrar")
    public String cuentasCobrar(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "cuentas-cobrar");
        model.addAttribute("usuario", usuario);
        return "cuentas-cobrar";
    }

    @GetMapping("/cuentas-pagar")
    public String cuentasPagar(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        model.addAttribute("currentPage", "cuentas-pagar");
        model.addAttribute("usuario", usuario);
        return "cuentas-pagar";
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
