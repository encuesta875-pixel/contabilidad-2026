package com.app.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad que representa cada línea (detalle) de un Asiento Contable
 * Cada línea tiene un débito O un crédito (nunca ambos)
 */
@Entity
@Table(name = "detalles_asiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Asiento contable al que pertenece este detalle
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asiento;

    /**
     * Cuenta contable afectada
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @NotNull(message = "La cuenta es obligatoria")
    private PlanCuentas cuenta;

    /**
     * Monto en DÉBITO (lado izquierdo)
     * Si es 0, entonces hay crédito
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal debito = BigDecimal.ZERO;

    /**
     * Monto en CRÉDITO (lado derecho)
     * Si es 0, entonces hay débito
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal credito = BigDecimal.ZERO;

    /**
     * Descripción específica de este movimiento
     * Ej: "Efectivo recibido por venta", "Costo de mercancía vendida"
     */
    @Column(length = 500)
    private String descripcion;

    /**
     * Orden de la línea en el asiento
     */
    @Column(nullable = false)
    private Integer orden = 0;

    /**
     * Verificar si es un movimiento de débito
     */
    public boolean esDebito() {
        return debito.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verificar si es un movimiento de crédito
     */
    public boolean esCredito() {
        return credito.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Obtener el monto (sin importar si es débito o crédito)
     */
    public BigDecimal getMonto() {
        return esDebito() ? debito : credito;
    }

    // Getters y Setters manuales para compatibilidad
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AsientoContable getAsiento() {
        return asiento;
    }

    public void setAsiento(AsientoContable asiento) {
        this.asiento = asiento;
    }

    public PlanCuentas getCuenta() {
        return cuenta;
    }

    public void setCuenta(PlanCuentas cuenta) {
        this.cuenta = cuenta;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    /**
     * Validar que solo haya débito O crédito, no ambos
     */
    @PrePersist
    @PreUpdate
    protected void validar() {
        if (debito.compareTo(BigDecimal.ZERO) > 0 && credito.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Un detalle no puede tener débito y crédito al mismo tiempo");
        }
        if (debito.compareTo(BigDecimal.ZERO) == 0 && credito.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Un detalle debe tener débito o crédito");
        }
    }
}
