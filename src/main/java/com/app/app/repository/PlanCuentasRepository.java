package com.app.app.repository;

import com.app.app.model.PlanCuentas;
import com.app.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanCuentasRepository extends JpaRepository<PlanCuentas, Long> {

    /**
     * Buscar todas las cuentas de un usuario ordenadas por código
     */
    List<PlanCuentas> findByUsuarioOrderByCodigoAsc(Usuario usuario);

    /**
     * Buscar cuentas activas de un usuario
     */
    List<PlanCuentas> findByUsuarioAndActivaTrueOrderByCodigoAsc(Usuario usuario);

    /**
     * Buscar por código y usuario
     */
    Optional<PlanCuentas> findByCodigoAndUsuario(String codigo, Usuario usuario);

    /**
     * Buscar cuentas por tipo
     */
    List<PlanCuentas> findByUsuarioAndTipoOrderByCodigoAsc(Usuario usuario, String tipo);

    /**
     * Buscar cuentas por nivel
     */
    List<PlanCuentas> findByUsuarioAndNivelOrderByCodigoAsc(Usuario usuario, Integer nivel);

    /**
     * Buscar cuentas que aceptan movimientos
     */
    List<PlanCuentas> findByUsuarioAndAceptaMovimientosTrueAndActivaTrueOrderByCodigoAsc(Usuario usuario);

    /**
     * Buscar subcuentas de una cuenta padre
     */
    List<PlanCuentas> findByCuentaPadreOrderByCodigoAsc(PlanCuentas cuentaPadre);

    /**
     * Buscar cuentas raíz (sin padre)
     */
    List<PlanCuentas> findByUsuarioAndCuentaPadreIsNullOrderByCodigoAsc(Usuario usuario);

    /**
     * Verificar si existe una cuenta con el código
     */
    boolean existsByCodigoAndUsuario(String codigo, Usuario usuario);

    /**
     * Buscar por nombre (búsqueda parcial)
     */
    @Query("SELECT p FROM PlanCuentas p WHERE p.usuario = :usuario AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY p.codigo")
    List<PlanCuentas> buscarPorNombre(@Param("usuario") Usuario usuario, @Param("nombre") String nombre);

    /**
     * Buscar por código (búsqueda parcial)
     */
    @Query("SELECT p FROM PlanCuentas p WHERE p.usuario = :usuario AND p.codigo LIKE CONCAT(:codigo, '%') ORDER BY p.codigo")
    List<PlanCuentas> buscarPorCodigo(@Param("usuario") Usuario usuario, @Param("codigo") String codigo);
}
