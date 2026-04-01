package com.app.app.repository;

import com.app.app.model.Suscripcion;
import com.app.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    Optional<Suscripcion> findByUsuario(Usuario usuario);

    Optional<Suscripcion> findByUsuarioAndActivaTrue(Usuario usuario);
}
