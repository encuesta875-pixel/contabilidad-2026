/**
 * Script para manejar las funcionalidades de licencia de prueba
 */

/**
 * Inicia una prueba de 7 días
 */
function iniciarPrueba() {
    const confirmacion = confirm('¿Deseas iniciar tu período de prueba de 7 días gratuitos?');

    if (!confirmacion) {
        return;
    }

    // Deshabilitar el botón para evitar múltiples clics
    const boton = document.getElementById('btnIniciarPrueba');
    if (boton) {
        boton.disabled = true;
        boton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Iniciando...';
    }

    fetch('/licencia/iniciar-prueba', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarAlerta('success', 'Prueba Iniciada', `
                ¡Felicidades! Tu período de prueba de 7 días ha comenzado.
                <br><strong>Expira el:</strong> ${formatearFecha(data.fechaExpiracion)}
                <br><strong>Días restantes:</strong> ${data.diasRestantes}
            `);

            // Redirigir al dashboard después de 2 segundos
            setTimeout(() => {
                window.location.href = '/dashboard';
            }, 2000);
        } else {
            mostrarAlerta('danger', 'Error', data.mensaje);
            if (boton) {
                boton.disabled = false;
                boton.innerHTML = '<i class="fas fa-play me-2"></i>Iniciar Prueba de 7 Días';
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarAlerta('danger', 'Error', 'Ocurrió un error al iniciar la prueba. Por favor, intenta de nuevo.');
        if (boton) {
            boton.disabled = false;
            boton.innerHTML = '<i class="fas fa-play me-2"></i>Iniciar Prueba de 7 Días';
        }
    });
}

/**
 * Verifica el estado actual de la licencia
 */
function verificarEstadoLicencia() {
    fetch('/licencia/estado')
        .then(response => response.json())
        .then(data => {
            if (data.valida) {
                mostrarAlerta('success', 'Licencia Válida', data.mensaje);
            } else {
                mostrarAlerta('warning', 'Licencia Inválida', data.mensaje);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarAlerta('danger', 'Error', 'No se pudo verificar el estado de la licencia.');
        });
}

/**
 * Obtiene información completa de la licencia
 */
function obtenerInfoLicencia() {
    return fetch('/licencia/info')
        .then(response => response.json())
        .catch(error => {
            console.error('Error al obtener info de licencia:', error);
            return null;
        });
}

/**
 * Actualiza el contador de días restantes en tiempo real
 */
function actualizarContadorDias() {
    const elementoDias = document.getElementById('diasRestantes');
    if (!elementoDias) return;

    obtenerInfoLicencia().then(data => {
        if (data && data.tieneRegistro && !data.expirada) {
            elementoDias.textContent = data.diasRestantes;

            // Cambiar color según días restantes
            const progreso = document.getElementById('progresoLicencia');
            if (progreso) {
                const porcentaje = (data.diasRestantes / 7) * 100;
                progreso.style.width = porcentaje + '%';

                progreso.classList.remove('bg-success', 'bg-warning', 'bg-danger');
                if (data.diasRestantes <= 2) {
                    progreso.classList.add('bg-danger');
                } else if (data.diasRestantes <= 4) {
                    progreso.classList.add('bg-warning');
                } else {
                    progreso.classList.add('bg-success');
                }
            }
        }
    });
}

/**
 * Muestra una alerta personalizada
 */
function mostrarAlerta(tipo, titulo, mensaje) {
    const contenedorAlertas = document.getElementById('alertasContainer') || document.body;

    const alerta = document.createElement('div');
    alerta.className = `alert alert-${tipo} alert-dismissible fade show`;
    alerta.setAttribute('role', 'alert');
    alerta.innerHTML = `
        <h5 class="alert-heading">${titulo}</h5>
        <p class="mb-0">${mensaje}</p>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    if (contenedorAlertas === document.body) {
        alerta.style.position = 'fixed';
        alerta.style.top = '20px';
        alerta.style.right = '20px';
        alerta.style.zIndex = '9999';
        alerta.style.maxWidth = '400px';
    }

    contenedorAlertas.appendChild(alerta);

    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        alerta.classList.remove('show');
        setTimeout(() => alerta.remove(), 150);
    }, 5000);
}

/**
 * Formatea una fecha ISO a formato legible
 */
function formatearFecha(fechaISO) {
    if (!fechaISO) return 'N/A';

    const fecha = new Date(fechaISO);
    const opciones = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };

    return fecha.toLocaleDateString('es-ES', opciones);
}

/**
 * Inicializa los listeners cuando el DOM esté listo
 */
document.addEventListener('DOMContentLoaded', function() {
    // Actualizar contador de días si existe
    if (document.getElementById('diasRestantes')) {
        actualizarContadorDias();
        // Actualizar cada hora
        setInterval(actualizarContadorDias, 3600000);
    }

    // Agregar listener al botón de iniciar prueba si existe
    const btnIniciarPrueba = document.getElementById('btnIniciarPrueba');
    if (btnIniciarPrueba) {
        btnIniciarPrueba.addEventListener('click', iniciarPrueba);
    }

    // Mostrar información de licencia en consola para debugging
    if (window.location.search.includes('debug=true')) {
        obtenerInfoLicencia().then(data => {
            console.log('Info de Licencia:', data);
        });
    }
});

/**
 * Exportar funciones para uso global
 */
window.licenciaApp = {
    iniciarPrueba,
    verificarEstadoLicencia,
    obtenerInfoLicencia,
    actualizarContadorDias
};
