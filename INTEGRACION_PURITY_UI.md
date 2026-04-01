# Integración Purity UI Dashboard - Sistema de Contabilidad

## Resumen
Se ha integrado exitosamente la plantilla **Purity UI Dashboard** con el sistema de contabilidad Spring Boot utilizando **Thymeleaf** como motor de plantillas. El sistema está configurado para Colombia con **pesos colombianos (COP)** como moneda.

## Cambios Realizados

### 1. Assets Estáticos
✅ **Ubicación**: `src/main/resources/static/`

- **CSS**: `css/purity/purity-main.css` - Estilos completos del dashboard
- **SVG**: `svg/` - Íconos del sistema (home, credit, document, stats, etc.)
- **Imágenes**: `img/` y `img/avatars/` - Imágenes y avatares

### 2. Templates Thymeleaf
✅ **Ubicación**: `src/main/resources/templates/`

#### Fragmentos Reutilizables (`fragments/layout.html`)
- **head**: Meta tags y enlaces a CSS
- **sidebar**: Menú lateral de navegación con logo y opciones
- **navbar**: Barra superior con breadcrumb, búsqueda y acciones
- **footer**: Pie de página
- **scripts**: Scripts JavaScript

#### Páginas Actualizadas
- ✅ **dashboard.html** - Panel principal con estadísticas y gráficos

### 3. Controladores Actualizados

Se agregó el atributo `currentPage` a todos los controladores para resaltar el menú activo:

- ✅ `DashboardController` - currentPage: "dashboard"
- ✅ `TransaccionController` - currentPage: "transacciones"
- ✅ `ChatController` - currentPage: "chat"
- ✅ `DocumentoController` - currentPage: "documentos"
- ✅ `PlanesController` - currentPage: "planes"

### 4. Servicios Mejorados

#### TransaccionService
- ✅ Nuevo método: `obtenerUltimasTransacciones(Usuario, int limite)`
  - Retorna las últimas N transacciones para mostrar en el dashboard

### 5. Configuración Regional

#### LocaleConfig.java
```java
// Configuración de localización para Colombia
localeResolver.setDefaultLocale(new Locale("es", "CO"));
```

✅ **Formato de Moneda**: Pesos Colombianos (COP)
✅ **Formato de Números**: Separador de miles: punto (.) - Decimales: coma (,)
✅ **Formato de Fechas**: dd/MM/yyyy

### 6. JavaScript
✅ **app.js** - Funcionalidades del frontend:
- Manejo del sidebar responsive
- Formateo de moneda colombiana
- Sistema de notificaciones toast

## Características del Dashboard

### Componentes Incluidos
1. **Sidebar Responsive**
   - Navegación principal
   - Logo personalizado
   - Información del usuario
   - Auto-colapsa en móviles

2. **Cards de Estadísticas**
   - Total Ingresos (COP)
   - Total Gastos (COP)
   - Balance Total (COP)
   - Íconos y colores distintivos

3. **Acciones Rápidas**
   - Iniciar Chat con IA
   - Nueva Transacción
   - Subir Documento

4. **Tabla de Actividad Reciente**
   - Últimas 5 transacciones
   - Formato de moneda en COP
   - Badges de estado

### Paleta de Colores (Purity UI)
```css
--primary: #4318FF (Púrpura)
--success: #01B574 (Verde)
--error: #E31A1A (Rojo)
--warning: #FFB547 (Naranja)
--info: #4299E1 (Azul)
```

## Uso en Nuevas Páginas

Para crear una nueva página con el diseño de Purity UI:

```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head th:replace="~{fragments/layout :: head('Título de la Página')}"></head>
<body>
    <div class="main-container">
        <!-- Sidebar -->
        <div th:replace="~{fragments/layout :: sidebar}"></div>

        <!-- Content Wrapper -->
        <div class="content-wrapper">
            <div class="content-main">
                <!-- Navbar -->
                <div th:replace="~{fragments/layout :: navbar('Título', 'Home / Título')}"></div>

                <!-- Tu contenido aquí -->
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Mi Card</h3>
                    </div>
                    <div class="card-body">
                        <p>Contenido...</p>
                    </div>
                </div>

                <!-- Footer -->
                <div th:replace="~{fragments/layout :: footer}"></div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### En el Controlador
```java
@GetMapping("/mi-pagina")
public String miPagina(Model model) {
    model.addAttribute("currentPage", "mi-pagina"); // Para resaltar en sidebar
    // ... resto del código
    return "mi-pagina";
}
```

## Clases CSS Útiles

### Layout
- `.grid` - Grid container
- `.grid-cols-2`, `.grid-cols-3`, `.grid-cols-4` - Columnas
- `.card` - Tarjeta base
- `.card-stat` - Tarjeta de estadística

### Botones
- `.btn` - Botón base
- `.btn-primary`, `.btn-secondary`, `.btn-success`, `.btn-danger`
- `.btn-sm`, `.btn-lg` - Tamaños

### Badges
- `.badge` - Badge base
- `.badge-success`, `.badge-error`, `.badge-warning`, `.badge-info`

### Utilidades
- `.text-primary`, `.text-success`, `.text-error`
- `.mt-1` a `.mt-4` - Márgenes superiores
- `.mb-1` a `.mb-4` - Márgenes inferiores
- `.d-flex`, `.justify-between`, `.align-center`

## Formato de Moneda

En Thymeleaf:
```html
<!-- Sin decimales (recomendado para COP) -->
$<span th:text="${#numbers.formatDecimal(monto, 1, 'COMMA', 0, 'POINT')}">0</span> COP

<!-- Con decimales -->
$<span th:text="${#numbers.formatDecimal(monto, 1, 'COMMA', 2, 'POINT')}">0.00</span> COP
```

En JavaScript:
```javascript
formatCOP(12345678); // "$12.345.678"
```

## Próximos Pasos

Páginas pendientes de actualizar con Purity UI:
- [ ] transacciones.html
- [ ] documentos.html
- [ ] planes.html
- [ ] chat.html
- [ ] login.html
- [ ] registro.html

## Notas de Desarrollo

1. **Responsive**: El diseño es totalmente responsive
2. **Accesibilidad**: Usa semantic HTML y ARIA labels
3. **Rendimiento**: CSS optimizado y JavaScript mínimo
4. **Mantenibilidad**: Fragmentos reutilizables con Thymeleaf

## Soporte

Para dudas o problemas:
- Revisar la documentación de Thymeleaf: https://www.thymeleaf.org/
- Revisar Purity UI original: https://demos.creative-tim.com/purity-ui-dashboard/

---
**Última actualización**: 31 de marzo de 2026
**Sistema**: App-Contabilidad v1.0
**País**: Colombia 🇨🇴
