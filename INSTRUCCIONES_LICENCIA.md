# 🚀 Instrucciones Rápidas - Sistema de Licencias

## 📦 Paso 1: Ejecutar Script SQL

Ejecuta el script de migración en tu base de datos MySQL:

```bash
mysql -u tu_usuario -p tu_base_de_datos < src/main/resources/db/migration/licencias_prueba.sql
```

O copia y ejecuta el contenido del archivo directamente en tu gestor de base de datos.

## 🔧 Paso 2: Verificar Configuración

Asegúrate de que tu `application.properties` tenga la configuración correcta de JPA:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🎨 Paso 3: Integrar en la Página de Planes

Agrega el siguiente código a tu archivo `planes.html`:

### En la sección `<head>`:

```html
<!-- Incluir el script de licencias -->
<script src="/js/licencia.js"></script>
```

### En el `<body>`, donde quieras mostrar la opción de prueba gratuita:

```html
<!-- Contenedor para alertas -->
<div id="alertasContainer" class="container mt-3"></div>

<!-- Mensaje de expiración si viene redirigido -->
<div th:if="${mensajeExpiracion}" class="alert alert-warning alert-dismissible fade show" role="alert">
    <h5 class="alert-heading">
        <i class="fas fa-exclamation-triangle me-2"></i>
        Período de Prueba Expirado
    </h5>
    <p class="mb-0" th:text="${mensajeExpiracion}"></p>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>

<!-- Card de Prueba Gratuita -->
<div th:if="${puedeIniciarPrueba}" class="card border-success mb-4">
    <div class="card-header bg-success text-white">
        <h5 class="mb-0">
            <i class="fas fa-gift me-2"></i>
            ¡Prueba Gratuita de 7 Días!
        </h5>
    </div>
    <div class="card-body">
        <h4>Comienza tu prueba gratuita</h4>
        <p class="text-muted">
            Accede a todas las funcionalidades de ContaPro durante 7 días sin costo alguno.
        </p>
        <ul class="list-unstyled">
            <li><i class="fas fa-check text-success me-2"></i>Acceso completo a todas las funcionalidades</li>
            <li><i class="fas fa-check text-success me-2"></i>Sin tarjeta de crédito requerida</li>
            <li><i class="fas fa-check text-success me-2"></i>7 días de prueba gratuita</li>
        </ul>
        <button id="btnIniciarPrueba" class="btn btn-success btn-lg">
            <i class="fas fa-play me-2"></i>Iniciar Prueba de 7 Días
        </button>
    </div>
</div>

<!-- Información de Licencia Activa -->
<div th:if="${tieneLicenciaPrueba && licenciaActiva}" class="alert alert-info">
    <h5 class="alert-heading">
        <i class="fas fa-info-circle me-2"></i>
        Período de Prueba Activo
    </h5>
    <p>
        Tienes <strong><span id="diasRestantes" th:text="${diasRestantes}">0</span> días</strong> restantes de prueba gratuita.
    </p>
    <div class="progress" style="height: 25px;">
        <div id="progresoLicencia"
             class="progress-bar"
             role="progressbar"
             th:style="'width: ' + ${(diasRestantes / 7.0) * 100} + '%'"
             th:classappend="${diasRestantes <= 2} ? 'bg-danger' : (${diasRestantes <= 4} ? 'bg-warning' : 'bg-success')">
            <span th:text="${diasRestantes} + ' / 7 días'"></span>
        </div>
    </div>
    <div class="mt-3">
        <a href="/licencia/info-page" class="btn btn-sm btn-outline-info">
            <i class="fas fa-info-circle me-1"></i>Ver Detalles de Licencia
        </a>
    </div>
</div>

<!-- Licencia Expirada -->
<div th:if="${tieneLicenciaPrueba && licenciaExpirada}" class="alert alert-danger">
    <h5 class="alert-heading">
        <i class="fas fa-times-circle me-2"></i>
        Período de Prueba Expirado
    </h5>
    <p class="mb-0">
        Tu período de prueba ha finalizado. Por favor, selecciona un plan para continuar.
    </p>
</div>
```

## 📱 Paso 4: Probar el Sistema

### 1. Verificar MAC Address del Sistema

Ejecuta en tu terminal:

**Windows:**
```cmd
ipconfig /all
```

**Linux/Mac:**
```bash
ifconfig
```

### 2. Iniciar la Aplicación

```bash
mvn spring-boot:run
```

### 3. Probar Endpoints

#### Iniciar prueba de 7 días:
```bash
curl -X POST http://localhost:8080/licencia/iniciar-prueba
```

Respuesta esperada:
```json
{
  "success": true,
  "mensaje": "Prueba de 7 días iniciada exitosamente",
  "diasRestantes": 7,
  "fechaExpiracion": "2026-04-08T..."
}
```

#### Verificar estado:
```bash
curl http://localhost:8080/licencia/estado
```

Respuesta esperada:
```json
{
  "valida": true,
  "mensaje": "Quedan 7 días de prueba",
  "diasRestantes": 7,
  "fechaExpiracion": "2026-04-08T..."
}
```

#### Obtener información:
```bash
curl http://localhost:8080/licencia/info
```

## 🔍 Paso 5: Verificar en Base de Datos

```sql
-- Ver licencias registradas
SELECT * FROM licencias_prueba;

-- Ver días restantes
SELECT
    mac_address,
    DATEDIFF(fecha_expiracion, NOW()) as dias_restantes,
    activa,
    bloqueada
FROM licencias_prueba;
```

## 🎯 Flujo de Usuario

### Primer Acceso (Usuario Nuevo)

1. Usuario se registra/inicia sesión
2. No tiene suscripción ni licencia de prueba
3. Es redirigido a `/planes?expired=true`
4. Ve la opción de "Iniciar Prueba de 7 Días"
5. Hace clic en el botón
6. Sistema obtiene MAC address automáticamente
7. Crea registro en `licencias_prueba`
8. Usuario es redirigido a `/dashboard`

### Durante el Período de Prueba

1. Usuario accede normalmente a la aplicación
2. El `LicenciaInterceptor` valida en cada petición
3. Si la licencia es válida, permite acceso
4. Si la licencia expiró, redirige a `/planes?expired=true`

### Después de la Expiración

1. Usuario intenta acceder a la aplicación
2. Sistema detecta que la licencia expiró
3. Marca la licencia como inactiva y bloqueada
4. Redirige a `/planes?expired=true`
5. Usuario debe adquirir una suscripción

## 🛠️ Personalización

### Cambiar duración de la prueba

Edita `LicenciaPrueba.java`:

```java
public LicenciaPrueba(String macAddress) {
    this.macAddress = macAddress;
    this.fechaInicio = LocalDateTime.now();
    this.fechaExpiracion = LocalDateTime.now().plusDays(14); // Cambiar a 14 días
    this.activa = true;
    this.bloqueada = false;
}
```

### Agregar rutas excluidas del interceptor

Edita `LicenciaInterceptor.java`:

```java
private boolean isPublicPath(String requestURI) {
    return requestURI.startsWith("/css/") ||
           requestURI.startsWith("/nueva-ruta/") || // Agregar aquí
           // ... otras rutas
}
```

## 🧪 Testing

### Caso 1: Iniciar Prueba

1. Accede a `/planes`
2. Haz clic en "Iniciar Prueba de 7 Días"
3. Deberías ver un mensaje de éxito
4. Verifica en la base de datos:
   ```sql
   SELECT * FROM licencias_prueba ORDER BY id DESC LIMIT 1;
   ```

### Caso 2: Verificar Licencia Activa

1. Con una licencia activa, accede a `/dashboard`
2. Deberías poder acceder sin problemas
3. Accede a `/licencia/info-page`
4. Deberías ver los detalles de tu licencia

### Caso 3: Simular Expiración

```sql
-- Forzar expiración para pruebas
UPDATE licencias_prueba
SET fecha_expiracion = NOW() - INTERVAL 1 DAY
WHERE mac_address = 'TU-MAC-ADDRESS';
```

Luego intenta acceder a `/dashboard`, deberías ser redirigido a `/planes`.

### Caso 4: Limpiar Licencia para Nueva Prueba

```sql
-- Solo para desarrollo/testing
DELETE FROM licencias_prueba WHERE mac_address = 'TU-MAC-ADDRESS';
```

## 📊 Monitoreo

### Ver estado de todas las licencias:

```sql
SELECT
    mac_address,
    fecha_inicio,
    fecha_expiracion,
    DATEDIFF(fecha_expiracion, NOW()) as dias_restantes,
    activa,
    bloqueada,
    motivo_bloqueo
FROM licencias_prueba
ORDER BY fecha_registro DESC;
```

### Ver licencias que expiran pronto:

```sql
SELECT * FROM licencias_prueba
WHERE DATEDIFF(fecha_expiracion, NOW()) <= 2
  AND activa = TRUE
  AND bloqueada = FALSE;
```

## ❓ Solución de Problemas

### Error: "No se pudo obtener la dirección MAC"

**Solución**: Verifica que tu interfaz de red esté activa y configurada.

### Error: "El período de prueba ha expirado para este dispositivo"

**Solución**: La MAC address ya tiene un registro expirado. En producción, esto es esperado. En desarrollo, puedes eliminar el registro.

### No se crea la tabla `licencias_prueba`

**Solución**: Ejecuta manualmente el script SQL o verifica que `spring.jpa.hibernate.ddl-auto=update` esté configurado.

## 📝 Notas Importantes

1. **Una licencia por dispositivo**: La MAC address es única por dispositivo
2. **No renovable**: Una vez expirada, no se puede reiniciar la prueba en el mismo dispositivo
3. **Bloqueo automático**: Las licencias expiradas se bloquean automáticamente
4. **Prioridad de suscripciones**: Si existe una suscripción activa, no se valida la licencia de prueba

## 📖 Documentación Completa

Para información más detallada, consulta: `SISTEMA_LICENCIAS.md`

---

**¡Listo!** Tu sistema de licencias de prueba está configurado y funcionando.
