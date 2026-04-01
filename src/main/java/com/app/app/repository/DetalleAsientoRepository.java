package com.app.app.repository;

import com.app.app.model.DetalleAsiento;
import com.app.app.model.PlanCuentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Long> {

    /**
     * Buscar detalles por cuenta (para el Libro Mayor)
     */
    @Query("SELECT d FROM DetalleAsiento d WHERE d.cuenta = :cuenta " +
           "AND d.asiento.estado = 'CONTABILIZADO' " +
           "ORDER BY d.asiento.fecha ASC, d.asiento.numeroAsiento ASC")
    List<DetalleAsiento> findByCuentaContabilizados(@Param("cuenta") PlanCuentas cuenta);

    /**
     * Buscar detalles por cuenta y rango de fechas
     */
    @Query("SELECT d FROM DetalleAsiento d WHERE d.cuenta = :cuenta " +
           "AND d.asiento.estado = 'CONTABILIZADO' " +
           "AND d.asiento.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY d.asiento.fecha ASC, d.asiento.numeroAsiento ASC")
    List<DetalleAsiento> findByCuentaYPeriodo(
            @Param("cuenta") PlanCuentas cuenta,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
