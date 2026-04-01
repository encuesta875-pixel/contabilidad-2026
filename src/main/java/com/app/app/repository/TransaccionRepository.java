package com.app.app.repository;

import com.app.app.model.Transaccion;
import com.app.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuarioOrderByFechaTransaccionDesc(Usuario usuario);

    List<Transaccion> findByUsuarioAndTipo(Usuario usuario, String tipo);

    @Query("SELECT SUM(t.monto) FROM Transaccion t WHERE t.usuario = ?1 AND t.tipo = ?2")
    BigDecimal calcularTotalPorTipo(Usuario usuario, String tipo);
}
