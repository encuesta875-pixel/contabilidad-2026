# 🎯 Lógica de Licencias y Suscripciones - Aclaración IMPORTANTE

## ⚠️ Corrección Importante

La implementación inicial tenía un error crítico de lógica de negocio que fue corregido:

### ❌ INCORRECTO (Versión Inicial)
- Si la licencia de prueba expira, el dispositivo se bloquea PERMANENTEMENTE
- No se puede adquirir suscripción de pago después de la prueba

### ✅ CORRECTO (Versión Actual)
- Si la licencia de prueba expira, NO se puede iniciar OTRA prueba gratuita
- SÍ se puede (y debe) adquirir una suscripción de PAGO en cualquier momento
- El bloqueo solo aplica para PRUEBAS GRATUITAS, no para suscripciones de pago

## 🔄 Flujo Correcto del Sistema

### 1️⃣ Usuario Nuevo (Primera Vez)

```
Usuario se registra
    ↓
No tiene suscripción
No tiene licencia de prueba
    ↓
Opciones:
    A) Iniciar prueba de 7 días gratis ✅
    B) Adquirir suscripción de pago directamente ✅
```

### 2️⃣ Durante el Período de Prueba (Días 1-7)

```
Usuario con licencia de prueba activa
    ↓
Interceptor verifica:
    1. ¿Tiene suscripción de pago? NO
    2. ¿Tiene licencia de prueba válida? SÍ
    ↓
ACCESO PERMITIDO ✅
    ↓
En cualquier momento puede:
    - Adquirir suscripción de pago ✅
    - Continuar con la prueba gratuita ✅
```

### 3️⃣ Licencia de Prueba Expirada (Día 8+)

```
Usuario con licencia de prueba expirada
    ↓
Interceptor verifica:
    1. ¿Tiene suscripción de pago? NO
    2. ¿Tiene licencia de prueba válida? NO (expirada)
    ↓
ACCESO BLOQUEADO ❌
Redirige a /planes
    ↓
Opciones del usuario:
    A) Iniciar OTRA prueba gratis? ❌ NO PERMITIDO
    B) Adquirir suscripción de pago? ✅ SÍ PERMITIDO
```

**IMPORTANTE**: El usuario SIEMPRE puede adquirir una suscripción de pago, incluso con licencia de prueba expirada.

### 4️⃣ Con Suscripción de Pago Activa

```
Usuario con suscripción de pago
    ↓
Interceptor verifica:
    1. ¿Tiene suscripción de pago? SÍ ✅
    ↓
ACCESO COMPLETO PERMITIDO ✅
(No verifica licencia de prueba)
    ↓
Estado de licencia de prueba: IRRELEVANTE
```

### 5️⃣ Suscripción de Pago Expirada

```
Usuario con suscripción expirada
    ↓
Interceptor verifica:
    1. ¿Tiene suscripción de pago activa? NO (expirada)
    2. ¿Tiene licencia de prueba válida? NO (ya usada)
    ↓
ACCESO BLOQUEADO ❌
Redirige a /planes
    ↓
Opciones:
    A) Renovar suscripción de pago ✅
    B) Adquirir nueva suscripción ✅
```

## 📊 Tabla de Decisiones del Interceptor

| Suscripción de Pago | Licencia de Prueba | Resultado | Acción |
|---------------------|-------------------|-----------|---------|
| ✅ Activa | ❓ Cualquiera | ✅ PERMITIR | Acceso completo |
| ❌ No activa | ✅ Válida (< 7 días) | ✅ PERMITIR | Acceso temporal |
| ❌ No activa | ❌ Expirada (> 7 días) | ❌ BLOQUEAR | Redirigir a /planes |
| ❌ No activa | ❓ Sin registro | ❌ BLOQUEAR | Redirigir a /planes |

## 🔑 Reglas Clave

### ✅ SIEMPRE Permitido
1. Adquirir una suscripción de pago (en cualquier momento)
2. Renovar una suscripción de pago
3. Acceder con suscripción de pago activa
4. Acceder con licencia de prueba válida (si no hay suscripción)

### ❌ NUNCA Permitido
1. Iniciar una segunda prueba gratuita en el mismo dispositivo
2. Acceder sin suscripción de pago Y sin licencia de prueba válida

### 🔄 Prioridades del Sistema
1. **Primera prioridad**: Suscripción de pago activa → Acceso total
2. **Segunda prioridad**: Licencia de prueba válida → Acceso temporal
3. **Sin ninguna**: Bloquear → Redirigir a /planes

## 💰 Lógica de Negocio

### Objetivo del Sistema
```
Prueba gratuita de 7 días
    ↓
Convencer al usuario del valor del producto
    ↓
Convertir a suscriptor de pago
```

### Lo que NO debe hacer el sistema
```
❌ Bloquear permanentemente a usuarios que quieren pagar
❌ Impedir la compra de suscripciones
❌ Penalizar a usuarios que probaron el producto
```

### Lo que SÍ debe hacer el sistema
```
✅ Ofrecer prueba gratuita de 7 días UNA VEZ por dispositivo
✅ SIEMPRE permitir la compra de suscripciones de pago
✅ Dar acceso completo a suscriptores de pago
✅ Bloquear solo a usuarios sin licencia válida que no quieren pagar
```

## 🛠️ Implementación Técnica

### LicenciaInterceptor.java
```java
// PRIORIDAD 1: Suscripción de pago
if (usuario.getSuscripcion() != null && usuario.getSuscripcion().getActiva()) {
    return true; // ✅ ACCESO TOTAL
}

// PRIORIDAD 2: Licencia de prueba
boolean licenciaValida = licenciaService.tieneLicenciaValida();
if (!licenciaValida) {
    response.sendRedirect("/planes?expired=true");
    return false; // ❌ REDIRIGIR A PLANES
}

return true; // ✅ ACCESO TEMPORAL
```

### SuscripcionService.java
```java
/**
 * IMPORTANTE: Este método PERMITE crear suscripciones
 * incluso si la licencia de prueba ha expirado.
 * El bloqueo de licencia de prueba NO afecta
 * la capacidad de adquirir suscripciones de pago.
 */
public Suscripcion crearOActualizarSuscripcion(...) {
    // NO verifica estado de licencia de prueba
    // SIEMPRE permite crear suscripción de pago
}
```

## 📝 Endpoints

### Para Licencias de Prueba
- `POST /licencia/iniciar-prueba` - Inicia prueba (solo primera vez)
- `GET /licencia/estado` - Verifica estado de la prueba

### Para Suscripciones de Pago
- `POST /suscripcion/adquirir` - Adquiere suscripción (SIEMPRE disponible)
- `POST /suscripcion/renovar` - Renueva suscripción
- `GET /suscripcion/estado` - Verifica estado de suscripción
- `POST /suscripcion/cancelar` - Cancela suscripción

## 🎨 Mensaje en la UI

### Cuando la prueba expira
```
❌ Mensaje INCORRECTO:
"Tu cuenta ha sido bloqueada. No puedes continuar."

✅ Mensaje CORRECTO:
"Tu período de prueba de 7 días ha finalizado.
¡Esperamos que hayas disfrutado la experiencia!

Para continuar usando ContaPro, selecciona un plan:
- Plan Mensual: $X.XX/mes
- Plan Trimestral: $X.XX/3 meses
- Plan Anual: $X.XX/año"
```

## 🧪 Casos de Prueba

### Caso 1: Usuario quiere pagar después de la prueba ✅
```
1. Usuario usa los 7 días de prueba
2. Prueba expira
3. Usuario intenta acceder → Bloqueado
4. Redirigido a /planes
5. Usuario selecciona plan y paga
6. POST /suscripcion/adquirir → ✅ ÉXITO
7. Usuario tiene acceso completo
```

### Caso 2: Usuario quiere otra prueba gratis ❌
```
1. Usuario usa los 7 días de prueba
2. Prueba expira
3. Usuario intenta iniciar otra prueba
4. POST /licencia/iniciar-prueba → ❌ ERROR
5. Mensaje: "Período de prueba ya utilizado. Adquiere una suscripción."
```

### Caso 3: Usuario adquiere suscripción antes de que expire la prueba ✅
```
1. Usuario inicia prueba (día 1)
2. Usuario le gusta el producto (día 3)
3. Usuario adquiere suscripción de pago
4. POST /suscripcion/adquirir → ✅ ÉXITO
5. Licencia de prueba se vuelve irrelevante
6. Usuario tiene acceso completo por suscripción de pago
```

## ✨ Resumen

| Situación | Puede Iniciar Prueba | Puede Adquirir Suscripción | Tiene Acceso |
|-----------|---------------------|---------------------------|--------------|
| Nuevo usuario | ✅ SÍ | ✅ SÍ | ❌ NO (hasta que haga una de las dos) |
| Con prueba activa | ❌ NO (ya tiene) | ✅ SÍ | ✅ SÍ (por prueba) |
| Prueba expirada | ❌ NO | ✅ SÍ | ❌ NO (debe pagar) |
| Con suscripción activa | ❓ Irrelevante | ✅ SÍ (renovar) | ✅ SÍ (por suscripción) |

**Mensaje Clave**: El bloqueo de licencia de prueba SOLO previene iniciar OTRA prueba gratis. NUNCA bloquea la compra de suscripciones de pago.

---

**Versión**: 1.1 (Corregida)
**Fecha**: 2026-04-01
