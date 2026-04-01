package com.app.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Asiento Contable
 * Implementa el principio de Partida Doble (Débito = Crédito)
 */
@Entity
@Table(name = "asientos_contables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número consecutivo del asiento
     * Ej: 1, 2, 3, 4...
     */
    @NotNull(message = "El número de asiento es obligatorio")
    @Column(nullable = false)
    private Long numeroAsiento;

    /**
     * Fecha del asiento contable
     */
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Concepto o descripción general del asiento
     * Ej: "Registro de venta de mercancía", "Pago de nómina"
     */
    @NotBlank(message = "El concepto es obligatorio")
    @Column(nullable = false, length = 500)
    private String concepto;

    /**
     * Estado del asiento
     * BORRADOR: En edición, no afecta los saldos
     * CONTABILIZADO: Definitivo, afecta los saldos
     * ANULADO: Cancelado
     */
    @NotBlank(message = "El estado es obligatorio")
    @Column(nullable = false, length = 20)
    private String estado = "BORRADOR";

    /**
     * Tipo de asiento
     * APERTURA, REGULAR, AJUSTE, CIERRE
     */
    @Column(length = 20)
    private String tipo = "REGULAR";

    /**
     * Documento de referencia
     * Ej: "Factura No. 1234", "Recibo No. 5678"
     */
    @Column(length = 100)
    private String documentoReferencia;

    /**
     * Suma total de débitos
     * Se calcula automáticamente de los detalles
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal totalDebito = BigDecimal.ZERO;

    /**
     * Suma total de créditos
     * Se calcula automáticamente de los detalles
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal totalCredito = BigDecimal.ZERO;

    /**
     * Indica si el asiento está cuadrado (Débito = Crédito)
     */
    @Column(nullable = false)
    private Boolean cuadrado = false;

    /**
     * Detalles del asiento (líneas de débito y crédito)
     */
    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAsiento> detalles = new ArrayList<>();

    /**
     * Usuario que creó el asiento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Fecha de creación
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Fecha de última modificación
     */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion = LocalDateTime.now();

    /**
     * Agregar un detalle al asiento
     */
    public void agregarDetalle(DetalleAsiento detalle) {
        detalles.add(detalle);
        detalle.setAsiento(this);
        recalcularTotales();
    }

    /**
     * Remover un detalle del asiento
     */
    public void removerDetalle(DetalleAsiento detalle) {
        detalles.remove(detalle);
        detalle.setAsiento(null);
        recalcularTotales();
    }

    /**
     * Recalcular totales y verificar si está cuadrado
     */
    public void recalcularTotales() {
        totalDebito = detalles.stream()
                .map(DetalleAsiento::getDebito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalCredito = detalles.stream()
                .map(DetalleAsiento::getCredito)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cuadrado = totalDebito.compareTo(totalCredito) == 0 &&
                   totalDebito.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verificar si el asiento puede ser contabilizado
     */
    public boolean puedeSerContabilizado() {
        return cuadrado &&
               estado.equals("BORRADOR") &&
               !detalles.isEmpty();
    }

    /**
     * Contabilizar el asiento (hacer definitivo)
     */
    public void contabilizar() {
        if (!puedeSerContabilizado()) {
            throw new IllegalStateException("El asiento no puede ser contabilizado");
        }
        estado = "CONTABILIZADO";
    }

    /**
     * Anular el asiento
     */
    public void anular() {
        if (estado.equals("ANULADO")) {
            throw new IllegalStateException("El asiento ya está anulado");
        }
        estado = "ANULADO";
    }

    /**
     * Verificar si es un asiento de apertura
     */
    public boolean esApertura() {
        return "APERTURA".equals(tipo);
    }

    /**
     * Verificar si es un asiento de cierre
     */
    public boolean esCierre() {
        return "CIERRE".equals(tipo);
    }

    // Getters y Setters manuales para compatibilidad
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(Long numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDocumentoReferencia() {
        return documentoReferencia;
    }

    public void setDocumentoReferencia(String documentoReferencia) {
        this.documentoReferencia = documentoReferencia;
    }

    public BigDecimal getTotalDebito() {
        return totalDebito;
    }

    public void setTotalDebito(BigDecimal totalDebito) {
        this.totalDebito = totalDebito;
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = totalCredito;
    }

    public Boolean getCuadrado() {
        return cuadrado;
    }

    public void setCuadrado(Boolean cuadrado) {
        this.cuadrado = cuadrado;
    }

    public List<DetalleAsiento> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleAsiento> detalles) {
        this.detalles = detalles;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
        recalcularTotales();
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        fechaModificacion = LocalDateTime.now();
        recalcularTotales();
    }
}
