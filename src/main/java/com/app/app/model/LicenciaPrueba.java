package com.app.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "licencias_prueba")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenciaPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mac_address", nullable = false, unique = true, length = 100)
    private String macAddress;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "bloqueada")
    private Boolean bloqueada = false;

    @Column(name = "motivo_bloqueo", length = 255)
    private String motivoBloqueo;

    // Constructor personalizado para crear una nueva licencia de prueba
    public LicenciaPrueba(String macAddress) {
        this.macAddress = macAddress;
        this.fechaInicio = LocalDateTime.now();
        this.fechaExpiracion = LocalDateTime.now().plusDays(7);
        this.activa = true;
        this.bloqueada = false;
    }

    // Método para verificar si la licencia ha expirado
    public boolean haExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    // Método para obtener días restantes
    public long getDiasRestantes() {
        if (haExpirado()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), fechaExpiracion).toDays();
    }

    // Getters y Setters explícitos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(Boolean bloqueada) {
        this.bloqueada = bloqueada;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void setMotivoBloqueo(String motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }
}
