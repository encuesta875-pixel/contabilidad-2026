# 💼 CONTA - Sistema de Gestión Contable con IA

## 📋 Descripción
CONTA es una aplicación web profesional de gestión contable con inteligencia artificial, diseñada para personas y emprendedores sin conocimientos contables previos. Simplifica la contabilidad mediante una interfaz conversacional.

## 🛠️ Tecnologías Utilizadas
- **Backend**: Spring Boot 4.0.5 (Java 17)
- **Frontend**: Thymeleaf + Bootstrap 5
- **Base de Datos**: MySQL
- **Seguridad**: Spring Security (BCrypt)
- **Arquitectura**: MVC (Model-View-Controller)

## 📁 Estructura del Proyecto
```
src/main/java/com/app/app/
├── model/              # Entidades JPA
│   ├── Usuario.java
│   ├── Transaccion.java
│   ├── Suscripcion.java
│   └── Documento.java
├── repository/         # Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── TransaccionRepository.java
│   ├── SuscripcionRepository.java
│   └── DocumentoRepository.java
├── service/            # Lógica de negocio
│   ├── UsuarioService.java
│   ├── TransaccionService.java
│   ├── ChatService.java
│   └── DocumentoService.java
├── controller/         # Controladores MVC
│   ├── HomeController.java
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── ChatController.java
│   ├── TransaccionController.java
│   ├── PlanesController.java
│   └── DocumentoController.java
└── config/             # Configuración
    └── SecurityConfig.java

src/main/resources/
├── templates/          # Vistas Thymeleaf
│   ├── login.html
│   ├── registro.html
│   ├── dashboard.html
│   ├── chat.html
│   ├── transacciones.html
│   ├── documentos.html
│   └── planes.html
└── static/css/         # Estilos
    └── style.css
```

## 🚀 Instrucciones de Instalación

### Prerrequisitos
1. **Java 17 o superior** instalado
2. **MySQL** instalado y ejecutándose
3. **Maven** instalado

### Paso 1: Configurar Base de Datos
Crear la base de datos MySQL:
```sql
CREATE DATABASE conta_db;
```

**Nota**: La configuración actual usa:
- Usuario: `root`
- Contraseña: `Panama507-`
- Puerto: `3306`

Si necesitas cambiar estas credenciales, edita `application.properties`.

### Paso 2: Compilar el Proyecto
```bash
cd App-Contabilidad
mvn clean install
```

### Paso 3: Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

O ejecutar el JAR generado:
```bash
java -jar target/App-contabilidadapp-Version1.jar
```

### Paso 4: Acceder a la Aplicación
Abrir el navegador en:
```
http://localhost:8080
```

## 👤 Funcionalidades Principales

### 1. Autenticación
- ✅ Registro de usuarios
- ✅ Login con Spring Security
- ✅ Encriptación BCrypt
- ✅ Control de sesiones

### 2. Dashboard
- ✅ Resumen financiero
- ✅ Métricas de ingresos, gastos y balance
- ✅ Acciones rápidas

### 3. Chat con IA
- ✅ Procesamiento de lenguaje natural
- ✅ Reconocimiento de frases cotidianas
- ✅ Registro automático de transacciones
- ✅ Ejemplos:
  - "Vendí 3 panes a $5000"
  - "Gasté $10000 en comida"
  - "¿Cuál es mi balance?"

### 4. Gestión de Transacciones
- ✅ CRUD completo
- ✅ Clasificación por tipo (Ingreso/Gasto)
- ✅ Filtros y búsqueda
- ✅ Tabla dinámica

### 5. Documentos
- ✅ Subida de archivos
- ✅ Gestión de documentos
- ✅ Eliminación segura

### 6. Planes de Suscripción
- ✅ Mensual: $8
- ✅ Trimestral: $19
- ✅ Anual: $83

## 🔒 Seguridad
- Spring Security configurado
- Encriptación de contraseñas con BCrypt
- Rutas protegidas
- Validaciones de formularios
- Control de acceso por sesión

## 🎨 Diseño
- Interfaz moderna tipo SaaS
- Responsive (Bootstrap 5)
- Colores: Azul (#4F46E5), Blanco, Gris
- Sidebar lateral fijo
- Navbar superior
- Cards con hover effects
- Gradientes modernos

## 📝 Notas Importantes

### Configuración de la Base de Datos
El archivo `application.properties` está configurado con:
- `spring.jpa.hibernate.ddl-auto=update` (crea tablas automáticamente)
- Las tablas se crean en el primer inicio

### Primer Usuario
Para crear tu primer usuario:
1. Ir a `http://localhost:8080/registro`
2. Completar el formulario
3. Iniciar sesión

### Chat con IA
El sistema reconoce patrones básicos en español:
- Palabras clave de venta: "vendí", "gané", "cobré", "ingresé"
- Palabras clave de gasto: "compré", "gasté", "pagué", "debí", "fiar"
- Extrae automáticamente montos con formato: $5000, 5.000, etc.

## 🐛 Solución de Problemas

### Error de conexión a MySQL
- Verificar que MySQL esté ejecutándose
- Confirmar credenciales en `application.properties`
- Verificar que exista la base de datos `conta_db`

### Puerto 8080 en uso
Cambiar el puerto en `application.properties`:
```properties
server.port=8081
```

### Error de dependencias
Ejecutar:
```bash
mvn clean install -U
```

## 📞 Soporte
Para dudas o problemas, contactar al equipo de desarrollo de CONTA.

## 📄 Licencia
Proyecto educativo - CONTA Startup

---
**Desarrollado con Spring Boot, Thymeleaf y ❤️**
