# Sistema de Licencias de Prueba - 7 Días

Este documento describe el sistema de licencias de prueba implementado en la aplicación de contabilidad.

## 📋 Descripción General

El sistema de licencias permite que los usuarios prueben la aplicación durante **7 días** de forma gratuita. La licencia está vinculada a la **dirección MAC** del dispositivo para evitar múltiples registros.

## 🔧 Componentes Implementados

### 1. Entidades

#### `LicenciaPrueba.java`
- **Ubicación**: `src/main/java/com/app/app/model/`
- **Campos principales**:
  - `macAddress`: Dirección MAC única del dispositivo
  - `fechaInicio`: Fecha de inicio de la prueba
  - `fechaExpiracion`: Fecha de expiración (7 días después del inicio)
  - `activa`: Estado de la licencia
  - `bloqueada`: Indica si la licencia fue bloqueada
  - `motivoBloqueo`: Razón del bloqueo

#### Actualización de `Suscripcion.java`
- Agregado campo `macAddress` para vincular suscripciones al dispositivo

### 2. Repositorios

#### `LicenciaPruebaRepository.java`
- Métodos para buscar licencias por dirección MAC
- Verificación de existencia de licencias

### 3. Servicios

#### `LicenciaService.java`
Servicio principal que maneja toda la lógica de licencias:

**Métodos principales**:
- `obtenerMacAddress()`: Obtiene la dirección MAC del dispositivo actual
- `tieneLicenciaValida()`: Verifica si existe una licencia válida
- `registrarLicenciaPrueba()`: Registra una nueva licencia de 7 días
- `obtenerLicenciaActual()`: Obtiene información de la licencia del dispositivo
- `verificarEstadoLicencia()`: Retorna estado detallado de la licencia

### 4. Interceptor

#### `LicenciaInterceptor.java`
- Intercepta todas las peticiones HTTP
- Verifica la licencia antes de permitir acceso a recursos protegidos
- Rutas excluidas:
  - Recursos estáticos (`/css/`, `/js/`, `/images/`)
  - Páginas públicas (`/login`, `/registro`, `/planes`)
  - OAuth2 endpoints
  - Páginas de error

### 5. Configuración

#### `WebMvcConfig.java`
Registra el interceptor de licencias en la aplicación.

### 6. Controlador

#### `LicenciaController.java`
Endpoints REST para gestionar licencias:

- `POST /licencia/iniciar-prueba`: Inicia una prueba de 7 días
- `GET /licencia/estado`: Verifica el estado actual de la licencia
- `GET /licencia/info`: Obtiene información detallada de la licencia
- `GET /licencia/info-page`: Página HTML con información de la licencia

## 🚀 Flujo de Funcionamiento

### 1. Primer Acceso (Sin Licencia)

```
Usuario accede a la aplicación
    ↓
LicenciaInterceptor verifica licencia
    ↓
No existe licencia registrada
    ↓
Redirige a /planes?expired=true
    ↓
Usuario inicia prueba (POST /licencia/iniciar-prueba)
    ↓
Sistema obtiene MAC address del dispositivo
    ↓
Crea registro en licencias_prueba
    ↓
Período de prueba: 7 días desde ahora
```

### 2. Accesos Posteriores (Con Licencia Activa)

```
Usuario accede a la aplicación
    ↓
LicenciaInterceptor verifica licencia
    ↓
Encuentra licencia activa y no expirada
    ↓
Permite acceso normal a la aplicación
```

### 3. Licencia Expirada

```
Usuario accede a la aplicación
    ↓
LicenciaInterceptor verifica licencia
    ↓
Licencia encontrada pero expirada (> 7 días)
    ↓
Marca licencia como inactiva y bloqueada
    ↓
Redirige a /planes?expired=true
    ↓
Usuario debe adquirir una suscripción
```

## 💾 Base de Datos

### Script de Migración

Ejecutar el script: `src/main/resources/db/migration/licencias_prueba.sql`

```sql
-- Crea tabla licencias_prueba
-- Agrega columna mac_address a suscripciones
-- Crea índices necesarios
```

## 🔒 Seguridad y Validaciones

1. **Una licencia por dispositivo**: La dirección MAC es única
2. **No se puede renovar**: Si la prueba expira, no se puede crear otra para ese dispositivo
3. **Bloqueo automático**: Las licencias expiradas se bloquean automáticamente
4. **Validación en cada petición**: El interceptor valida en cada request
5. **Prioridad de suscripciones**: Si existe suscripción activa, no se valida licencia de prueba

## 📝 Uso desde el Frontend

### Iniciar Prueba de 7 Días

```javascript
fetch('/licencia/iniciar-prueba', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => {
    if (data.success) {
        console.log('Prueba iniciada:', data.mensaje);
        console.log('Días restantes:', data.diasRestantes);
        console.log('Expira:', data.fechaExpiracion);
    } else {
        console.error('Error:', data.mensaje);
    }
});
```

### Verificar Estado de la Licencia

```javascript
fetch('/licencia/estado')
    .then(response => response.json())
    .then(data => {
        if (data.valida) {
            console.log(data.mensaje); // "Quedan X días de prueba"
            console.log('Días restantes:', data.diasRestantes);
        } else {
            console.log('Licencia inválida:', data.mensaje);
            // Redirigir a planes
        }
    });
```

### Obtener Información Completa

```javascript
fetch('/licencia/info')
    .then(response => response.json())
    .then(data => {
        console.log('MAC Address:', data.macAddress);
        console.log('Tiene registro:', data.tieneRegistro);
        if (data.tieneRegistro) {
            console.log('Activa:', data.activa);
            console.log('Bloqueada:', data.bloqueada);
            console.log('Días restantes:', data.diasRestantes);
        }
    });
```

## ⚙️ Configuración

### Cambiar Duración de la Prueba

Modificar en `LicenciaPrueba.java`:

```java
public LicenciaPrueba(String macAddress) {
    this.macAddress = macAddress;
    this.fechaInicio = LocalDateTime.now();
    this.fechaExpiracion = LocalDateTime.now().plusDays(7); // Cambiar aquí
    this.activa = true;
    this.bloqueada = false;
}
```

### Excluir Rutas del Interceptor

Modificar `WebMvcConfig.java` o `LicenciaInterceptor.java`:

```java
private boolean isPublicPath(String requestURI) {
    return requestURI.startsWith("/css/") ||
           requestURI.startsWith("/nueva-ruta-publica/") || // Agregar aquí
           // ... otras rutas
}
```

## 🧪 Testing

### Verificar MAC Address del Sistema

```bash
# Windows
ipconfig /all

# Linux/Mac
ifconfig
```

### Limpiar Licencias de Prueba (Desarrollo)

```sql
DELETE FROM licencias_prueba WHERE mac_address = 'XX-XX-XX-XX-XX-XX';
```

## 📊 Consultas Útiles

### Ver todas las licencias de prueba

```sql
SELECT * FROM licencias_prueba;
```

### Ver licencias activas

```sql
SELECT * FROM licencias_prueba WHERE activa = TRUE AND bloqueada = FALSE;
```

### Ver licencias expiradas

```sql
SELECT * FROM licencias_prueba WHERE fecha_expiracion < NOW();
```

### Ver días restantes de todas las licencias

```sql
SELECT
    mac_address,
    DATEDIFF(fecha_expiracion, NOW()) as dias_restantes,
    activa,
    bloqueada
FROM licencias_prueba;
```

## 🔄 Integración con Sistema de Suscripciones

El sistema está diseñado para trabajar en conjunto con las suscripciones:

1. **Sin suscripción activa**: Valida licencia de prueba
2. **Con suscripción activa**: Ignora licencia de prueba, permite acceso completo
3. **Suscripción vencida**: Vuelve a validar licencia de prueba

## ⚠️ Consideraciones Importantes

1. **Cambio de hardware**: Si cambia la tarjeta de red, cambiará la MAC address
2. **Máquinas virtuales**: Pueden tener MACs virtuales que cambien
3. **Múltiples interfaces**: Se usa la interfaz principal del sistema
4. **Docker/Contenedores**: La MAC puede ser diferente en cada contenedor

## 🛠️ Mantenimiento

### Limpiar licencias expiradas (Tarea programada recomendada)

```java
@Scheduled(cron = "0 0 2 * * *") // Ejecutar a las 2 AM diariamente
public void limpiarLicenciasExpiradas() {
    List<LicenciaPrueba> expiradas = licenciaPruebaRepository
        .findAll()
        .stream()
        .filter(LicenciaPrueba::haExpirado)
        .collect(Collectors.toList());

    expiradas.forEach(licencia -> {
        licencia.setActiva(false);
        licencia.setBloqueada(true);
        licencia.setMotivoBloqueo("Período de prueba expirado");
    });

    licenciaPruebaRepository.saveAll(expiradas);
}
```

## 📞 Soporte

Para más información sobre el sistema de licencias, contactar al equipo de desarrollo.

---

**Versión**: 1.0
**Última actualización**: 2026-04-01
