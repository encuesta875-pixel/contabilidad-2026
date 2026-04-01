package com.app.app.repository;

import com.app.app.model.AsientoContable;
import com.app.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {

    /**
     * Buscar todos los asientos de un usuario ordenados por fecha desc
     */
    List<AsientoContable> findByUsuarioOrderByFechaDescNumeroAsientoDesc(Usuario usuario);

    /**
     * Buscar asientos por estado
     */
    List<AsientoContable> findByUsuarioAndEstadoOrderByFechaDescNumeroAsientoDesc(Usuario usuario, String estado);

    /**
     * Buscar asientos por rango de fechas
     */
    List<AsientoContable> findByUsuarioAndFechaBetweenOrderByFechaAscNumeroAsientoAsc(
            Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Buscar asiento por número
     */
    Optional<AsientoContable> findByUsuarioAndNumeroAsiento(Usuario usuario, Long numeroAsiento);

    /**
     * Obtener el último número de asiento del usuario
     */
    @Query("SELECT MAX(a.numeroAsiento) FROM AsientoContable a WHERE a.usuario = :usuario")
    Long obtenerUltimoNumeroAsiento(@Param("usuario") Usuario usuario);

    /**
     * Contar asientos por estado
     */
    long countByUsuarioAndEstado(Usuario usuario, String estado);

    /**
     * Buscar asientos contabilizados en un periodo
     */
    @Query("SELECT a FROM AsientoContable a WHERE a.usuario = :usuario AND a.estado = 'CONTABILIZADO' " +
           "AND a.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fecha ASC, a.numeroAsiento ASC")
    List<AsientoContable> findAsientosContabilizadosPorPeriodo(
            @Param("usuario") Usuario usuario,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
