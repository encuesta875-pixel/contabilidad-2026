package com.app.app.service;

import com.app.app.model.Transaccion;
import com.app.app.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    @Autowired
    private TransaccionService transaccionService;

    public String procesarMensaje(String mensaje, Usuario usuario) {
        mensaje = mensaje.toLowerCase().trim();

        // Detectar venta/ingreso
        if (mensaje.contains("vend") || mensaje.contains("gan") || mensaje.contains("cobr")
            || mensaje.contains("ingres")) {
            return procesarIngreso(mensaje, usuario);
        }

        // Detectar gasto/deuda
        if (mensaje.contains("compr") || mensaje.contains("gast") || mensaje.contains("pag")
            || mensaje.contains("deb") || mensaje.contains("fiar")) {
            return procesarGasto(mensaje, usuario);
        }

        // Consulta de balance
        if (mensaje.contains("balance") || mensaje.contains("cuánto") || mensaje.contains("tengo")) {
            return consultarBalance(usuario);
        }

        return "No entendí tu mensaje. Puedes decirme cosas como: 'vendí 3 panes a $5000' o 'gasté $10000 en comida'";
    }

    private String procesarIngreso(String mensaje, Usuario usuario) {
        BigDecimal monto = extraerMonto(mensaje);
        String descripcion = extraerDescripcion(mensaje);

        if (monto != null && monto.compareTo(BigDecimal.ZERO) > 0) {
            Transaccion transaccion = new Transaccion();
            transaccion.setTipo("INGRESO");
            transaccion.setMonto(monto);
            transaccion.setDescripcion(descripcion);
            transaccion.setUsuario(usuario);

            transaccionService.crearTransaccion(transaccion);

            return String.format("✅ ¡Perfecto! Registré un ingreso de $%,.2f por: %s",
                monto, descripcion);
        }

        return "No pude detectar el monto. ¿Podrías especificarlo? Ej: 'vendí por $5000'";
    }

    private String procesarGasto(String mensaje, Usuario usuario) {
        BigDecimal monto = extraerMonto(mensaje);
        String descripcion = extraerDescripcion(mensaje);

        if (monto != null && monto.compareTo(BigDecimal.ZERO) > 0) {
            Transaccion transaccion = new Transaccion();
            transaccion.setTipo("GASTO");
            transaccion.setMonto(monto);
            transaccion.setDescripcion(descripcion);
            transaccion.setUsuario(usuario);

            transaccionService.crearTransaccion(transaccion);

            return String.format("✅ Registrado! Gasto de $%,.2f por: %s",
                monto, descripcion);
        }

        return "No pude detectar el monto. ¿Podrías especificarlo? Ej: 'gasté $5000 en comida'";
    }

    private String consultarBalance(Usuario usuario) {
        var resumen = transaccionService.calcularResumen(usuario);
        return String.format(
            "📊 Tu resumen financiero:\n" +
            "💰 Ingresos: $%,.2f\n" +
            "💸 Gastos: $%,.2f\n" +
            "📈 Balance: $%,.2f",
            resumen.get("totalIngresos"),
            resumen.get("totalGastos"),
            resumen.get("balance")
        );
    }

    private BigDecimal extraerMonto(String mensaje) {
        // Buscar patrones como $5000, 5000, $5.000
        Pattern pattern = Pattern.compile("\\$?\\s*(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)");
        Matcher matcher = pattern.matcher(mensaje);

        if (matcher.find()) {
            String montoStr = matcher.group(1).replace(".", "").replace(",", ".");
            try {
                return new BigDecimal(montoStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String extraerDescripcion(String mensaje) {
        // Simplemente devuelve el mensaje completo como descripción
        return mensaje.length() > 100 ? mensaje.substring(0, 97) + "..." : mensaje;
    }
}
