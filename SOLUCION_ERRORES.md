# 🔧 SOLUCIÓN DE ERRORES - CONTA

## ❌ Error: Unable to resolve name [org.hibernate.dialect.MySQL8Dialect]

### 📋 Problema:
Spring Boot 4.0.5 usa Hibernate 7.x, donde los dialectos de MySQL han cambiado.

### ✅ Solución Aplicada:
He actualizado `application.properties`:

**ANTES:**
```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**DESPUÉS:**
```properties
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

---

## 🔍 VERIFICACIÓN PREVIA AL INICIO

### 1. Verificar que MySQL está corriendo:

**Windows:**
```cmd
# Opción 1: Verificar servicio
sc query MySQL80

# Opción 2: Verificar proceso
tasklist | findstr mysql

# Si no está corriendo, iniciarlo:
net start MySQL80
```

**Linux/Mac:**
```bash
# Verificar estado
sudo systemctl status mysql

# Iniciar si está detenido
sudo systemctl start mysql
```

---

### 2. Crear la Base de Datos (si no existe):

```bash
# Conectarse a MySQL
mysql -u root -p

# Ingresa la contraseña: Panama507-

# Crear base de datos
CREATE DATABASE IF NOT EXISTS conta_db;

# Verificar que existe
SHOW DATABASES;

# Salir
EXIT;
```

---

### 3. Verificar Conexión:

```bash
mysql -u root -pPanama507- -e "SELECT 1"
```

Si sale error de conexión, verifica:
- ✅ MySQL está corriendo
- ✅ Usuario: `root`
- ✅ Password: `Panama507-`
- ✅ Puerto: `3306`

---

## 🚀 EJECUTAR LA APLICACIÓN

### Paso 1: Compilar
```bash
mvn clean package -DskipTests
```

### Paso 2: Ejecutar
```bash
mvn spring-boot:run
```

### Paso 3: Verificar logs
Busca en la consola:
```
Tomcat started on port 8080
Started AppContabilidadApplication
```

### Paso 4: Acceder
```
http://localhost:8080
```

---

## 🐛 OTROS ERRORES COMUNES

### Error: "Access denied for user 'root'@'localhost'"

**Solución:** Verificar contraseña en `application.properties`

```properties
spring.datasource.password=Panama507-
```

Si la contraseña es diferente, actualízala.

---

### Error: "Communications link failure"

**Causas posibles:**
1. MySQL no está corriendo
2. Puerto incorrecto
3. Firewall bloqueando

**Solución:**
```bash
# Verificar puerto
netstat -an | findstr 3306

# Si usa otro puerto, actualizar application.properties:
spring.datasource.url=jdbc:mysql://localhost:PUERTO/conta_db
```

---

### Error: "Unknown database 'conta_db'"

**Solución:** Crear la base de datos manualmente:

```sql
CREATE DATABASE conta_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### Error: "Table 'conta_db.usuarios' doesn't exist"

**Esto es NORMAL** en el primer inicio. Spring Boot creará las tablas automáticamente con:
```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## ✅ CHECKLIST ANTES DE EJECUTAR

- [ ] MySQL instalado y corriendo
- [ ] Base de datos `conta_db` creada
- [ ] Usuario `root` con password `Panama507-` funciona
- [ ] Puerto 3306 disponible
- [ ] Puerto 8080 disponible (para Tomcat)
- [ ] Java 17 instalado
- [ ] Maven configurado

---

## 🎯 VERIFICACIÓN RÁPIDA

Ejecuta este comando para verificar todo:

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar MySQL (Windows)
mysql -u root -pPanama507- -e "SHOW DATABASES;"

# Compilar proyecto
mvn clean compile
```

Si todo funciona, ejecuta:
```bash
mvn spring-boot:run
```

---

## 📞 SI PERSISTE EL ERROR

1. **Ver logs completos:**
   ```bash
   mvn spring-boot:run > logs.txt 2>&1
   ```

2. **Verificar el error específico** en `logs.txt`

3. **Errores comunes:**
   - `BeanCreationException` → Problema de configuración
   - `SQLException` → Problema de base de datos
   - `BindException` → Puerto en uso

---

**¡El dialecto ya está corregido!** Ahora intenta ejecutar nuevamente:

```bash
mvn spring-boot:run
```
