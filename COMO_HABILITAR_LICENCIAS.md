# 🚀 Cómo Habilitar el Sistema de Licencias

## ⚠️ IMPORTANTE: El Sistema Está Deshabilitado Por Defecto

Para evitar que la aplicación se "pegue" o bloquee, el sistema de licencias está **DESHABILITADO** por defecto.

La aplicación funcionará **normalmente sin restricciones** hasta que lo habilites.

## 📋 Estado Actual

```
app.licencias.habilitado=false  ← Sistema DESHABILITADO
```

Esto significa:
- ✅ La aplicación funciona normalmente
- ✅ No hay validación de licencias
- ✅ No se verifica MAC address
- ✅ Todos los usuarios tienen acceso completo

## 🔧 Pasos para Habilitar el Sistema de Licencias

### Paso 1: Crear la Tabla en la Base de Datos ⚠️ OBLIGATORIO

Antes de habilitar el sistema, **DEBES** crear la tabla `licencias_prueba`:

#### Opción A: Usar el Script SQL (Recomendado)

```bash
mysql -u root -p conta_db < src/main/resources/db/migration/licencias_prueba.sql
```

O si prefieres hacerlo manualmente:

```bash
mysql -u root -p
```

```sql
USE conta_db;

-- Crear tabla de licencias de prueba
CREATE TABLE IF NOT EXISTS licencias_prueba (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mac_address VARCHAR(100) NOT NULL UNIQUE,
    fecha_inicio DATETIME NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    bloqueada BOOLEAN DEFAULT FALSE,
    motivo_bloqueo VARCHAR(255),
    INDEX idx_mac_address (mac_address),
    INDEX idx_activa (activa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Agregar columna mac_address a suscripciones
ALTER TABLE suscripciones
ADD COLUMN IF NOT EXISTS mac_address VARCHAR(100);

-- Crear índice
CREATE INDEX IF NOT EXISTS idx_suscripciones_mac_address
ON suscripciones(mac_address);
```

#### Opción B: Dejar que Hibernate la Cree (Automático)

Si tienes `spring.jpa.hibernate.ddl-auto=update` en tu `application.properties`, Hibernate **intentará** crear la tabla automáticamente.

**NOTA:** Esto puede fallar en algunos casos, por eso es mejor usar el script SQL.

### Paso 2: Verificar que la Tabla Existe

```sql
USE conta_db;
SHOW TABLES LIKE 'licencias_prueba';
DESCRIBE licencias_prueba;
```

Deberías ver:
```
+-------------------+--------------+------+-----+---------+----------------+
| Field             | Type         | Null | Key | Default | Extra          |
+-------------------+--------------+------+-----+---------+----------------+
| id                | bigint       | NO   | PRI | NULL    | auto_increment |
| mac_address       | varchar(100) | NO   | UNI | NULL    |                |
| fecha_inicio      | datetime     | NO   |     | NULL    |                |
| fecha_expiracion  | datetime     | NO   |     | NULL    |                |
| activa            | tinyint(1)   | NO   |     | 1       |                |
| fecha_registro    | datetime     | YES  |     | current |                |
| bloqueada         | tinyint(1)   | YES  |     | 0       |                |
| motivo_bloqueo    | varchar(255) | YES  |     | NULL    |                |
+-------------------+--------------+------+-----+---------+----------------+
```

### Paso 3: Habilitar el Sistema de Licencias

Edita `src/main/resources/application.properties`:

```properties
# Cambiar de false a true
app.licencias.habilitado=true
```

### Paso 4: Reiniciar la Aplicación

```bash
# Detener la aplicación (Ctrl+C)
# Luego reiniciar:
mvn spring-boot:run
```

Deberías ver en los logs:
```
✅ Sistema de Licencias HABILITADO
```

### Paso 5: Probar el Sistema

1. **Accede a la aplicación:**
   ```
   http://localhost:8080
   ```

2. **Verifica que te redirija a /planes**
   - Si no tienes licencia, te redirigirá a `/planes?expired=true`

3. **Inicia una prueba:**
   - Ve a Planes
   - Haz clic en "Iniciar Prueba de 7 Días"

4. **Verifica en la base de datos:**
   ```sql
   SELECT * FROM licencias_prueba;
   ```

## 🔄 Cómo Deshabilitar el Sistema (Si Hay Problemas)

Si el sistema causa problemas, simplemente desactívalo:

```properties
# En application.properties
app.licencias.habilitado=false
```

Reinicia la aplicación y funcionará sin restricciones.

## 🧪 Modo de Prueba Seguro

El sistema ahora tiene **protección contra errores**:

1. **Si no existe la tabla `licencias_prueba`:**
   - ✅ La aplicación NO se bloquea
   - ✅ Muestra un error en los logs
   - ✅ Permite acceso normal

2. **Si no se puede obtener la MAC address:**
   - ✅ Usa un identificador alternativo basado en el sistema
   - ✅ No lanza excepción
   - ✅ La aplicación continúa funcionando

3. **Si hay cualquier error en el interceptor:**
   - ✅ Captura la excepción
   - ✅ Muestra el error en los logs
   - ✅ Permite acceso (fail-safe)

## 📊 Verificar Estado del Sistema

### Verificar si está habilitado:

Al iniciar la aplicación, busca en los logs:

```
✅ Sistema de Licencias HABILITADO
```

O:

```
⚠️ Sistema de Licencias DESHABILITADO - La aplicación funcionará sin restricciones
```

### Probar endpoints (con sistema habilitado):

```bash
# Verificar MAC address
curl http://localhost:8080/licencia/info

# Iniciar prueba
curl -X POST http://localhost:8080/licencia/iniciar-prueba

# Ver estado
curl http://localhost:8080/licencia/estado
```

## ❓ Solución de Problemas

### Problema: "La aplicación se queda pegada"

**Solución:**
```properties
# Deshabilitar temporalmente
app.licencias.habilitado=false
```

### Problema: "Error: Table 'conta_db.licencias_prueba' doesn't exist"

**Solución:**
```bash
# Ejecutar el script SQL
mysql -u root -p conta_db < src/main/resources/db/migration/licencias_prueba.sql
```

### Problema: "No se puede obtener MAC address"

**Solución:**
- El sistema ahora usa un identificador alternativo automáticamente
- No debería causar errores

### Problema: "Todos los usuarios tienen acceso completo"

**Verificar:**
```properties
# Debe estar en true
app.licencias.habilitado=true
```

## 📝 Resumen

| Estado | Configuración | Comportamiento |
|--------|--------------|----------------|
| **Deshabilitado** | `app.licencias.habilitado=false` | Sin restricciones, acceso completo |
| **Habilitado** | `app.licencias.habilitado=true` | Valida licencias y suscripciones |

**Recomendación:** Mantener deshabilitado hasta que hayas creado la tabla y probado que funciona correctamente.

---

**Última actualización:** 2026-04-01
