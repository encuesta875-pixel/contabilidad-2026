package com.app.app.service;

import com.app.app.model.Documento;
import com.app.app.model.Usuario;
import com.app.app.repository.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    private final String UPLOAD_DIR = "uploads/documentos/";

    @Transactional
    public Documento subirDocumento(MultipartFile file, Usuario usuario) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único
        String nombreOriginal = file.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Crear registro en BD
        Documento documento = new Documento();
        documento.setNombreArchivo(nombreOriginal);
        documento.setRuta(filePath.toString());
        documento.setTipoArchivo(file.getContentType());
        documento.setTamano(file.getSize());
        documento.setFechaSubida(LocalDateTime.now());
        documento.setUsuario(usuario);

        return documentoRepository.save(documento);
    }

    public List<Documento> listarPorUsuario(Usuario usuario) {
        return documentoRepository.findByUsuarioOrderByFechaSubidaDesc(usuario);
    }

    @Transactional
    public void eliminarDocumento(Long id) throws IOException {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        // Eliminar archivo físico
        Path filePath = Paths.get(documento.getRuta());
        Files.deleteIfExists(filePath);

        // Eliminar registro de BD
        documentoRepository.deleteById(id);
    }
}
