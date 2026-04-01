package com.app.app.service;

import com.app.app.model.Transaccion;
import com.app.app.model.Usuario;
import com.app.app.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Transactional
    public Transaccion crearTransaccion(Transaccion transaccion) {
        transaccion.setFechaTransaccion(LocalDateTime.now());
        return transaccionRepository.save(transaccion);
    }

    public List<Transaccion> listarPorUsuario(Usuario usuario) {
        return transaccionRepository.findByUsuarioOrderByFechaTransaccionDesc(usuario);
    }

    public Transaccion obtenerPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }

    @Transactional
    public void eliminarTransaccion(Long id) {
        transaccionRepository.deleteById(id);
    }

    public Map<String, BigDecimal> calcularResumen(Usuario usuario) {
        BigDecimal totalIngresos = transaccionRepository.calcularTotalPorTipo(usuario, "INGRESO");
        BigDecimal totalGastos = transaccionRepository.calcularTotalPorTipo(usuario, "GASTO");

        if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;
        if (totalGastos == null) totalGastos = BigDecimal.ZERO;

        BigDecimal balance = totalIngresos.subtract(totalGastos);

        Map<String, BigDecimal> resumen = new HashMap<>();
        resumen.put("totalIngresos", totalIngresos);
        resumen.put("totalGastos", totalGastos);
        resumen.put("balance", balance);

        return resumen;
    }

    public List<Transaccion> obtenerUltimasTransacciones(Usuario usuario, int limite) {
        List<Transaccion> todasTransacciones = transaccionRepository.findByUsuarioOrderByFechaTransaccionDesc(usuario);
        return todasTransacciones.size() > limite
            ? todasTransacciones.subList(0, limite)
            : todasTransacciones;
    }
}
