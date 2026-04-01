# 🎨 Ejemplo de Integración en el Frontend

Este documento muestra cómo integrar el sistema de licencias y suscripciones en la página de planes.

## 📄 Página de Planes Completa (`planes.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planes - ContaPro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <script src="/js/licencia.js"></script>
</head>
<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Nuestros Planes</h1>

        <!-- Contenedor para alertas -->
        <div id="alertasContainer"></div>

        <!-- MENSAJE DE EXPIRACIÓN -->
        <div th:if="${mensajeExpiracion}" class="alert alert-warning alert-dismissible fade show" role="alert">
            <h5 class="alert-heading">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Tu Período de Prueba ha Finalizado
            </h5>
            <p class="mb-0" th:text="${mensajeExpiracion}"></p>
            <p class="mt-2 mb-0">
                <strong>¡Buenas noticias!</strong> Puedes continuar usando ContaPro adquiriendo cualquiera de nuestros planes a continuación.
            </p>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>

        <div class="row">
            <!-- OPCIÓN 1: PRUEBA GRATUITA (Solo si puede iniciarla) -->
            <div th:if="${puedeIniciarPrueba}" class="col-md-12 mb-4">
                <div class="card border-success shadow-lg">
                    <div class="card-header bg-success text-white text-center">
                        <h3 class="mb-0">
                            <i class="fas fa-gift me-2"></i>
                            ¡Prueba Gratuita de 7 Días!
                        </h3>
                    </div>
                    <div class="card-body text-center p-4">
                        <h4 class="mb-3">Comienza sin compromiso</h4>
                        <p class="text-muted mb-4">
                            Accede a todas las funcionalidades de ContaPro durante 7 días sin costo alguno.
                            No necesitas tarjeta de crédito.
                        </p>
                        <ul class="list-unstyled text-start mb-4">
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Acceso completo a todas las funcionalidades
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Sin tarjeta de crédito requerida
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Soporte técnico incluido
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Cancela en cualquier momento
                            </li>
                        </ul>
                        <button id="btnIniciarPrueba" class="btn btn-success btn-lg">
                            <i class="fas fa-play me-2"></i>
                            Iniciar Prueba de 7 Días
                        </button>
                        <p class="text-muted mt-3 small mb-0">
                            Una vez finalizada la prueba, puedes adquirir cualquier plan para continuar.
                        </p>
                    </div>
                </div>
            </div>

            <!-- INFORMACIÓN DE LICENCIA ACTIVA -->
            <div th:if="${tieneLicenciaPrueba && licenciaActiva}" class="col-md-12 mb-4">
                <div class="alert alert-info shadow">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h5 class="alert-heading">
                                <i class="fas fa-clock me-2"></i>
                                Período de Prueba Activo
                            </h5>
                            <p class="mb-2">
                                Tienes <strong><span id="diasRestantes" th:text="${diasRestantes}">0</span> días</strong>
                                restantes de tu prueba gratuita.
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
                        </div>
                        <div class="col-md-4 text-end">
                            <a href="/licencia/info-page" class="btn btn-outline-info">
                                <i class="fas fa-info-circle me-1"></i>
                                Ver Detalles
                            </a>
                        </div>
                    </div>
                    <div class="mt-3 p-3 bg-light rounded">
                        <p class="mb-0 small">
                            <strong>💡 ¿Te gusta ContaPro?</strong> No esperes a que expire tu prueba.
                            Adquiere un plan ahora y asegura tu acceso sin interrupciones.
                        </p>
                    </div>
                </div>
            </div>

            <!-- PLANES DE PAGO -->
            <div class="col-md-12 mb-4">
                <h3 class="text-center mb-4">Planes de Suscripción</h3>
                <p class="text-center text-muted mb-4">
                    Selecciona el plan que mejor se adapte a tus necesidades
                </p>
            </div>

            <!-- Plan Mensual -->
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow">
                    <div class="card-header bg-primary text-white text-center">
                        <h4 class="mb-0">Plan Mensual</h4>
                    </div>
                    <div class="card-body d-flex flex-column">
                        <div class="text-center mb-4">
                            <h2 class="display-4">$29.99</h2>
                            <p class="text-muted">por mes</p>
                        </div>
                        <ul class="list-unstyled mb-4 flex-grow-1">
                            <li class="mb-2">
                                <i class="fas fa-check text-primary me-2"></i>
                                Acceso completo a todas las funcionalidades
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-primary me-2"></i>
                                Soporte técnico prioritario
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-primary me-2"></i>
                                Actualizaciones automáticas
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-primary me-2"></i>
                                Almacenamiento ilimitado
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-primary me-2"></i>
                                Cancela cuando quieras
                            </li>
                        </ul>
                        <button class="btn btn-primary btn-lg w-100 btnAdquirirPlan"
                                data-plan="MENSUAL"
                                data-precio="29.99">
                            <i class="fas fa-shopping-cart me-2"></i>
                            Adquirir Plan
                        </button>
                    </div>
                </div>
            </div>

            <!-- Plan Trimestral -->
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow border-warning" style="border-width: 3px;">
                    <div class="card-header bg-warning text-dark text-center position-relative">
                        <span class="badge bg-danger position-absolute top-0 start-50 translate-middle">
                            Más Popular
                        </span>
                        <h4 class="mb-0 mt-2">Plan Trimestral</h4>
                    </div>
                    <div class="card-body d-flex flex-column">
                        <div class="text-center mb-4">
                            <h2 class="display-4">$79.99</h2>
                            <p class="text-muted">por 3 meses</p>
                            <p class="text-success fw-bold">
                                <i class="fas fa-tag me-1"></i>
                                Ahorra 11%
                            </p>
                        </div>
                        <ul class="list-unstyled mb-4 flex-grow-1">
                            <li class="mb-2">
                                <i class="fas fa-check text-warning me-2"></i>
                                Todo lo del plan mensual
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-warning me-2"></i>
                                Reportes avanzados
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-warning me-2"></i>
                                Asesoría personalizada
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-warning me-2"></i>
                                Integraciones premium
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-star text-warning me-2"></i>
                                Precio especial
                            </li>
                        </ul>
                        <button class="btn btn-warning btn-lg w-100 btnAdquirirPlan"
                                data-plan="TRIMESTRAL"
                                data-precio="79.99">
                            <i class="fas fa-shopping-cart me-2"></i>
                            Adquirir Plan
                        </button>
                    </div>
                </div>
            </div>

            <!-- Plan Anual -->
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow border-success" style="border-width: 2px;">
                    <div class="card-header bg-success text-white text-center">
                        <h4 class="mb-0">Plan Anual</h4>
                    </div>
                    <div class="card-body d-flex flex-column">
                        <div class="text-center mb-4">
                            <h2 class="display-4">$299.99</h2>
                            <p class="text-muted">por año</p>
                            <p class="text-success fw-bold">
                                <i class="fas fa-tag me-1"></i>
                                Ahorra 17%
                            </p>
                        </div>
                        <ul class="list-unstyled mb-4 flex-grow-1">
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Todo lo del plan trimestral
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Acceso prioritario a nuevas funciones
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Capacitación incluida
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-check text-success me-2"></i>
                                Soporte 24/7
                            </li>
                            <li class="mb-2">
                                <i class="fas fa-crown text-warning me-2"></i>
                                Máximo ahorro
                            </li>
                        </ul>
                        <button class="btn btn-success btn-lg w-100 btnAdquirirPlan"
                                data-plan="ANUAL"
                                data-precio="299.99">
                            <i class="fas fa-shopping-cart me-2"></i>
                            Adquirir Plan
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Comparación de Planes -->
        <div class="row mt-5">
            <div class="col-12">
                <h3 class="text-center mb-4">Comparación de Planes</h3>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Característica</th>
                                <th class="text-center">Prueba Gratuita</th>
                                <th class="text-center">Mensual</th>
                                <th class="text-center">Trimestral</th>
                                <th class="text-center">Anual</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Duración</td>
                                <td class="text-center">7 días</td>
                                <td class="text-center">1 mes</td>
                                <td class="text-center">3 meses</td>
                                <td class="text-center">12 meses</td>
                            </tr>
                            <tr>
                                <td>Funcionalidades completas</td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                            </tr>
                            <tr>
                                <td>Soporte técnico</td>
                                <td class="text-center">Básico</td>
                                <td class="text-center">Prioritario</td>
                                <td class="text-center">Prioritario</td>
                                <td class="text-center">24/7</td>
                            </tr>
                            <tr>
                                <td>Reportes avanzados</td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                            </tr>
                            <tr>
                                <td>Integraciones premium</td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                            </tr>
                            <tr>
                                <td>Capacitación</td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-times text-danger"></i></td>
                                <td class="text-center"><i class="fas fa-check text-success"></i></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Manejar clic en botones de adquirir plan
        document.querySelectorAll('.btnAdquirirPlan').forEach(btn => {
            btn.addEventListener('click', function() {
                const plan = this.dataset.plan;
                const precio = this.dataset.precio;
                adquirirSuscripcion(plan, precio);
            });
        });

        // Función para adquirir suscripción
        function adquirirSuscripcion(tipoPlan, precio) {
            const confirmacion = confirm(
                `¿Deseas adquirir el Plan ${tipoPlan} por $${precio}?`
            );

            if (!confirmacion) return;

            fetch('/suscripcion/adquirir', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `tipoPlan=${tipoPlan}&precio=${precio}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('¡Suscripción adquirida exitosamente! Redirigiendo al dashboard...');
                    window.location.href = '/dashboard';
                } else {
                    alert('Error: ' + data.mensaje);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Ocurrió un error al procesar la suscripción.');
            });
        }
    </script>
</body>
</html>
```

## 🎯 Puntos Clave de la Integración

### 1. **Condiciones Thymeleaf**
```html
<!-- Mostrar botón de prueba SOLO si puede iniciarla -->
<div th:if="${puedeIniciarPrueba}">
    <button id="btnIniciarPrueba">Iniciar Prueba</button>
</div>

<!-- Mostrar días restantes si tiene licencia activa -->
<div th:if="${tieneLicenciaPrueba && licenciaActiva}">
    <span id="diasRestantes" th:text="${diasRestantes}"></span> días
</div>

<!-- Mensaje de expiración -->
<div th:if="${mensajeExpiracion}">
    <p th:text="${mensajeExpiracion}"></p>
</div>
```

### 2. **Botones de Acción**
```html
<!-- Botón para iniciar prueba (maneja licencia.js) -->
<button id="btnIniciarPrueba" class="btn btn-success">
    Iniciar Prueba de 7 Días
</button>

<!-- Botones para adquirir planes (manejan suscripciones) -->
<button class="btnAdquirirPlan"
        data-plan="MENSUAL"
        data-precio="29.99">
    Adquirir Plan
</button>
```

### 3. **Scripts Necesarios**
```html
<!-- En el <head> -->
<script src="/js/licencia.js"></script>

<!-- Al final del <body> -->
<script>
    // JavaScript para manejar adquisición de planes
</script>
```

---

Este ejemplo completo muestra cómo integrar ambos sistemas (licencias y suscripciones) en una sola página de planes.
