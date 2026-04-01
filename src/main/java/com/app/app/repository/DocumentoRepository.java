package com.app.app.repository;

import com.app.app.model.Documento;
import com.app.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByUsuarioOrderByFechaSubidaDesc(Usuario usuario);
}
