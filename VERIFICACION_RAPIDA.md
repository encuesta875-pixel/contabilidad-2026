# ✅ Verificación Rápida - ¿La Aplicación Funciona?

## 🚀 Pasos para Verificar

### 1️⃣ Inicia la Aplicación

```bash
cd C:\Users\dmojica\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\App-Contabilidad
mvn spring-boot:run
```

### 2️⃣ Busca Este Mensaje en los Logs

Deberías ver:

```
⚠️ Sistema de Licencias DESHABILITADO - La aplicación funcionará sin restricciones
```

Si ves este mensaje = ✅ **CORRECTO** - La aplicación funcionará normalmente.

### 3️⃣ Accede a la Aplicación

Abre tu navegador:
```
http://localhost:8080
```

### 4️⃣ Verifica que NO se Quede Pegada

- ✅ La página carga normalmente
- ✅ Puedes navegar entre páginas
- ✅ Puedes hacer clic en botones
- ✅ No hay pantallas en blanco

## ✅ Si Todo Funciona

**¡Perfecto!** La aplicación está funcionando correctamente sin el sistema de licencias.

### Cuando Quieras Habilitar el Sistema de Licencias:

1. Lee: `COMO_HABILITAR_LICENCIAS.md`
2. Crea la tabla en MySQL
3. Cambia `app.licencias.habilitado=true`
4. Reinicia la aplicación

## ❌ Si Aún se Queda Pegada

### Opción 1: Verifica la Configuración

Archivo: `src/main/resources/application.properties`

Busca esta línea:
```properties
app.licencias.habilitado=false
```

**DEBE estar en `false`**

### Opción 2: Verifica los Logs

Si al iniciar la aplicación ves errores, cópialos y compártelos.

Busca específicamente:
- ❌ `ERROR en LicenciaInterceptor:`
- ❌ `Table 'conta_db.licencias_prueba' doesn't exist`
- ❌ `Error al obtener la dirección MAC`

### Opción 3: Compilar de Nuevo

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

## 🔍 Diagnóstico

### ¿Cómo saber si el sistema de licencias está deshabilitado?

**Al iniciar la aplicación, busca en los logs:**

✅ **Correcto (Deshabilitado):**
```
⚠️ Sistema de Licencias DESHABILITADO - La aplicación funcionará sin restricciones
```

❌ **Incorrecto (Habilitado):**
```
✅ Sistema de Licencias HABILITADO
```

Si ves el mensaje "HABILITADO" y no has creado la tabla, **cámbialo a `false`**:

```properties
# En application.properties
app.licencias.habilitado=false
```

## 📊 Resumen de Estados

| Configuración | Estado | Comportamiento |
|---------------|--------|----------------|
| `app.licencias.habilitado=false` | ✅ Deshabilitado | La app funciona normalmente, sin restricciones |
| `app.licencias.habilitado=true` | ⚠️ Habilitado | Requiere tabla `licencias_prueba` en la BD |

## 🆘 Problemas Comunes

### 1. "La aplicación se queda pegada en localhost:8080"

**Solución:**
```properties
# Asegúrate de que esté en false
app.licencias.habilitado=false
```

### 2. "No arranca la aplicación"

**Posibles causas:**
- Puerto 8080 ocupado
- Base de datos no accesible
- Error en `application.properties`

**Solución rápida:**
```bash
# Ver qué está ocupando el puerto 8080
netstat -ano | findstr :8080

# Matar el proceso (reemplaza PID con el número que aparece)
taskkill /PID <numero> /F

# Verificar conexión a MySQL
mysql -u root -p
```

### 3. "Error: Could not autowire LicenciaService"

**Esto NO debería pasar**, pero si pasa:

**Solución:**
Asegúrate de que todos los archivos estén en su lugar:
- `LicenciaService.java` en `src/main/java/com/app/app/service/`
- `LicenciaPruebaRepository.java` en `src/main/java/com/app/app/repository/`
- `LicenciaPrueba.java` en `src/main/java/com/app/app/model/`

## 📝 Checklist de Verificación

Marca lo que has completado:

- [ ] Compilación exitosa (`mvn clean compile`)
- [ ] `application.properties` tiene `app.licencias.habilitado=false`
- [ ] Aplicación arranca sin errores
- [ ] Ves el mensaje "Sistema de Licencias DESHABILITADO"
- [ ] Puedes acceder a `http://localhost:8080`
- [ ] La aplicación NO se queda pegada
- [ ] Puedes navegar entre páginas

Si marcaste todos ✅ = **¡Todo funciona correctamente!**

## 🎯 Siguiente Paso

Una vez que confirmes que todo funciona:

1. **Lee:** `COMO_HABILITAR_LICENCIAS.md`
2. **Cuando estés listo:** Crea la tabla y habilita el sistema
3. **Por ahora:** Disfruta de la aplicación funcionando normalmente

---

**Fecha:** 2026-04-01
**Estado:** Sistema de Licencias OPCIONAL (Deshabilitado por defecto)
