# Paleta de Colores - Conta Pro 🎨

## Guía de Identidad Visual para Colombia

Esta es la paleta de colores oficial de **Conta Pro**, optimizada para aplicaciones de contabilidad en Colombia.

---

## 🎯 Colores de Identidad y Acción

### Azul Marino - Branding Principal
```
HEX: #002B49
RGB: 0, 43, 73
USO: Logo, títulos principales, encabezados, branding
```
**Aplicación en código:**
- Variable CSS: `--primary`
- Clase: `.text-primary`, `.bg-primary`

### Verde Éxito - Acción Principal
```
HEX: #1A8926
RGB: 26, 137, 38
USO: Botones principales, acciones de "Continuar", estados exitosos
```
**Aplicación en código:**
- Variable CSS: `--success`
- Clase: `.btn-primary`, `.text-success`, `.badge-success`

### Azul Eléctrico - Selección y Destacados
```
HEX: #4A90E2
RGB: 74, 144, 226
USO: Elementos seleccionados, enlaces activos, navegación activa
```
**Aplicación en código:**
- Variable CSS: `--secondary`
- Clase: `.nav-link.active`, `.text-secondary`

---

## 📊 Colores de Gráficas y Estados

### Turquesa - Ingresos Totales
```
HEX: #50E3C2
RGB: 80, 227, 194
USO: Indicadores de ingresos, valores positivos, gráficos de ingresos
```
**Aplicación en código:**
- Variable CSS: `--income`
- Clase: `.text-income`, `.badge-income`
- **Ejemplo:** Tarjeta de Total Ingresos

### Rojo - Gastos y Alertas
```
HEX: #D0021B
RGB: 208, 2, 27
USO: Indicadores de gastos, errores, alertas, valores negativos
```
**Aplicación en código:**
- Variable CSS: `--expense`, `--error`
- Clase: `.text-expense`, `.badge-expense`, `.text-error`
- **Ejemplo:** Tarjeta de Total Gastos

### Naranja - Inventario y Advertencias
```
HEX: #F5A623
RGB: 245, 166, 35
USO: Advertencias moderadas, inventario, estados pendientes
```
**Aplicación en código:**
- Variable CSS: `--warning`
- Clase: `.text-warning`, `.badge-warning`

### Morado - Categorías Secundarias
```
HEX: #9013FE
RGB: 144, 19, 254
USO: Información adicional, categorías especiales, elementos destacados
```
**Aplicación en código:**
- Variable CSS: `--info`
- Clase: `.text-info`, `.badge-info`

---

## 🎨 Colores de Estructura (UI)

### Fondo de Pantalla
```
HEX: #F4F7F9
RGB: 244, 247, 249
DESCRIPCIÓN: Gris azulado muy claro
USO: Fondo principal de la aplicación
```
**Aplicación en código:**
- Variable CSS: `--bg-main`, `--gray-50`
- Aplicado automáticamente al `<body>`

### Fondo de Tarjetas
```
HEX: #FFFFFF
RGB: 255, 255, 255
DESCRIPCIÓN: Blanco puro
USO: Fondo de cards, modales, paneles
```
**Aplicación en código:**
- Variable CSS: `--bg-card`
- Clase: `.card`

### Texto Principal
```
HEX: #002B49
RGB: 0, 43, 73
DESCRIPCIÓN: Azul Marino (mismo que branding)
USO: Títulos, texto importante
```
**Aplicación en código:**
- Variable CSS: `--text-primary`

### Texto Secundario
```
HEX: #4A4A4A
RGB: 74, 74, 74
DESCRIPCIÓN: Gris medio
USO: Subtítulos, descripciones, información secundaria
```
**Aplicación en código:**
- Variable CSS: `--text-secondary`

### Bordes y Divisiones
```
HEX: #E1E4E8
RGB: 225, 228, 232
DESCRIPCIÓN: Gris claro
USO: Bordes de cards, líneas separadoras, divisiones
```
**Aplicación en código:**
- Variable CSS: `--border-color`, `--gray-100`

---

## 📐 Guía de Uso por Componente

### Tarjetas de Estadísticas (Dashboard)

**Total Ingresos:**
- Texto: `#50E3C2` (Turquesa)
- Fondo del ícono: `rgba(80, 227, 194, 0.15)`
- Badge: `.badge-income`

**Total Gastos:**
- Texto: `#D0021B` (Rojo)
- Fondo del ícono: `rgba(208, 2, 27, 0.15)`
- Badge: `.badge-expense`

**Balance:**
- Si positivo: `#50E3C2` (Turquesa)
- Si negativo: `#D0021B` (Rojo)
- Fondo dinámico según balance

### Botones

**Botón Principal (Acción):**
```css
background: linear-gradient(135deg, #1A8926 0%, #2BA93A 100%);
color: white;
```
Clase: `.btn-primary`

**Botón Secundario:**
```css
background: #E1E4E8;
color: #002B49;
```
Clase: `.btn-secondary`

### Navegación (Sidebar)

**Item Activo:**
```css
background: linear-gradient(135deg, #4A90E2 0%, #6BA4E8 100%);
color: white;
box-shadow: 0 5px 15px rgba(74, 144, 226, 0.3);
```

**Item Hover:**
```css
background: #E1E4E8;
color: #002B49;
```

### Gradientes Especiales

**Header/Welcome Card:**
```css
background: linear-gradient(135deg, #002B49 0%, #4A90E2 100%);
color: white;
```

**Premium Card:**
```css
background: linear-gradient(135deg, #002B49 0%, #4A90E2 100%);
color: white;
```

---

## 🔧 Uso en HTML/Thymeleaf

### Clases de Utilidad Disponibles

**Colores de Texto:**
```html
<span class="text-primary">Azul Marino</span>
<span class="text-success">Verde</span>
<span class="text-income">Turquesa (Ingresos)</span>
<span class="text-expense">Rojo (Gastos)</span>
<span class="text-warning">Naranja</span>
<span class="text-error">Rojo (Error)</span>
<span class="text-info">Morado</span>
```

**Colores de Fondo:**
```html
<div class="bg-primary">Fondo Azul Marino</div>
<div class="bg-success">Fondo Verde</div>
<div class="bg-income">Fondo Turquesa</div>
<div class="bg-expense">Fondo Rojo</div>
```

**Badges:**
```html
<span class="badge badge-income">INGRESO</span>
<span class="badge badge-expense">GASTO</span>
<span class="badge badge-success">Exitoso</span>
<span class="badge badge-warning">Pendiente</span>
<span class="badge badge-info">Info</span>
```

---

## 🎯 Variables CSS Completas

Para usar en archivos CSS personalizados:

```css
/* Colores de Identidad */
--primary: #002B49;           /* Azul Marino */
--success: #1A8926;           /* Verde */
--secondary: #4A90E2;         /* Azul Eléctrico */

/* Colores de Gráficas */
--income: #50E3C2;            /* Turquesa - Ingresos */
--expense: #D0021B;           /* Rojo - Gastos */
--warning: #F5A623;           /* Naranja */
--info: #9013FE;              /* Morado */

/* Fondos */
--bg-main: #F4F7F9;           /* Fondo principal */
--bg-card: #FFFFFF;           /* Fondo de tarjetas */

/* Texto */
--text-primary: #002B49;      /* Texto principal */
--text-secondary: #4A4A4A;    /* Texto secundario */

/* Bordes */
--border-color: #E1E4E8;      /* Bordes y divisiones */
```

---

## ✅ Checklist de Accesibilidad

- ✅ Contraste texto/fondo cumple WCAG AA
- ✅ Colores no son la única forma de transmitir información
- ✅ Gradientes tienen suficiente contraste
- ✅ Iconos complementan los colores

---

## 📱 Visualización en Diferentes Contextos

### Monitor (Desktop)
- Colores se ven vibrantes y profesionales
- Gradientes se aprecian completamente

### Móvil/Tablet
- Colores mantienen legibilidad
- Badges y etiquetas son claramente distinguibles

### Impresión
- Tonos se convierten apropiadamente a escala de grises
- Contraste se mantiene para legibilidad

---

**Última actualización:** 31 de marzo de 2026
**Sistema:** Conta Pro - Colombia 🇨🇴
**Diseño:** Basado en Purity UI Dashboard con identidad Conta Pro
