package com.app.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el Plan de Cuentas (Catálogo Contable)
 * Base fundamental de todo sistema de contabilidad
 */
@Entity
@Table(name = "plan_cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanCuentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único de la cuenta contable
     * Ej: 1105 (Caja), 1110 (Bancos), 4135 (Comercio al por mayor)
     */
    @NotBlank(message = "El código de cuenta es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    /**
     * Nombre descriptivo de la cuenta
     * Ej: "Caja General", "Bancos Moneda Nacional"
     */
    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Tipo de cuenta según clasificación contable
     * ACTIVO, PASIVO, PATRIMONIO, INGRESO, GASTO, COSTO
     */
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Column(nullable = false, length = 20)
    private String tipo;

    /**
     * Nivel jerárquico de la cuenta
     * Nivel 1: Clase (Ej: 1 - ACTIVO)
     * Nivel 2: Grupo (Ej: 11 - DISPONIBLE)
     * Nivel 3: Cuenta (Ej: 1105 - CAJA)
     * Nivel 4: Subcuenta (Ej: 110505 - CAJA GENERAL)
     */
    @NotNull(message = "El nivel es obligatorio")
    @Column(nullable = false)
    private Integer nivel;

    /**
     * Naturaleza de la cuenta (DEBITO o CREDITO)
     * Determina en qué lado aumenta el saldo
     */
    @NotBlank(message = "La naturaleza es obligatoria")
    @Column(nullable = false, length = 10)
    private String naturaleza;

    /**
     * Indica si la cuenta acepta movimientos directos
     * Las cuentas de nivel superior (1, 2) suelen ser solo de agrupación
     */
    @Column(nullable = false)
    private Boolean aceptaMovimientos = true;

    /**
     * Estado de la cuenta (ACTIVA o INACTIVA)
     */
    @Column(nullable = false)
    private Boolean activa = true;

    /**
     * Descripción adicional de la cuenta
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Cuenta padre en la jerarquía
     * Para estructura de árbol
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_padre_id")
    private PlanCuentas cuentaPadre;

    /**
     * Cuentas hijas (subcuentas)
     */
    @OneToMany(mappedBy = "cuentaPadre", cascade = CascadeType.ALL)
    private List<PlanCuentas> subcuentas = new ArrayList<>();

    /**
     * Usuario propietario del plan de cuentas
     * Permite plan de cuentas personalizado por empresa
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Fecha de creación del registro
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Fecha de última modificación
     */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion = LocalDateTime.now();

    /**
     * Método helper para obtener el código completo con padding
     * Ej: 1105 -> "1105", 11 -> "11"
     */
    public String getCodigoFormateado() {
        return codigo;
    }

    /**
     * Método helper para obtener el nombre completo con jerarquía
     * Ej: "1 - ACTIVO > 11 - DISPONIBLE > 1105 - CAJA"
     */
    public String getNombreCompleto() {
        if (cuentaPadre != null) {
            return cuentaPadre.getNombreCompleto() + " > " + codigo + " - " + nombre;
        }
        return codigo + " - " + nombre;
    }

    /**
     * Método para verificar si es cuenta de nivel 1 (Clase)
     */
    public boolean esClase() {
        return nivel == 1;
    }

    /**
     * Método para verificar si es cuenta de nivel 2 (Grupo)
     */
    public boolean esGrupo() {
        return nivel == 2;
    }

    /**
     * Método para verificar si es cuenta de movimiento (nivel 3+)
     */
    public boolean esCuentaMovimiento() {
        return nivel >= 3 && aceptaMovimientos;
    }

    // Getters y Setters adicionales para campos críticos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public Boolean getAceptaMovimientos() {
        return aceptaMovimientos;
    }

    public void setAceptaMovimientos(Boolean aceptaMovimientos) {
        this.aceptaMovimientos = aceptaMovimientos;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PlanCuentas getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(PlanCuentas cuentaPadre) {
        this.cuentaPadre = cuentaPadre;
    }

    public List<PlanCuentas> getSubcuentas() {
        return subcuentas;
    }

    public void setSubcuentas(List<PlanCuentas> subcuentas) {
        this.subcuentas = subcuentas;
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
    }

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        fechaModificacion = LocalDateTime.now();
    }
}
