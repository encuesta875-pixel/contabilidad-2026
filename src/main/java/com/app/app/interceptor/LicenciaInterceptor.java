package com.app.app.interceptor;

import com.app.app.model.Usuario;
import com.app.app.service.LicenciaService;
import com.app.app.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LicenciaInterceptor implements HandlerInterceptor {

    @Autowired
    private LicenciaService licenciaService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Permitir acceso a recursos estáticos, login, registro y páginas de error
        String requestURI = request.getRequestURI();

        if (isPublicPath(requestURI)) {
            return true;
        }

        // Verificar si el usuario está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return true; // Dejar que Spring Security maneje la autenticación
        }

        // MANEJO DE ERRORES: Si hay cualquier error, permitir acceso
        // Esto evita que la aplicación se bloquee por problemas de licencia
        try {
            // Obtener el usuario autenticado
            String email = auth.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email).orElse(null);

            // Si el usuario no existe, permitir continuar (Spring Security lo manejará)
            if (usuario == null) {
                return true;
            }

            // PRIORIDAD 1: Verificar si el usuario tiene una suscripción PAGADA activa
            // Si tiene suscripción de pago activa, permitir acceso COMPLETO sin verificar licencia de prueba
            if (usuario.getSuscripcion() != null && usuario.getSuscripcion().getActiva()) {
                // Usuario con suscripción de pago activa - ACCESO TOTAL PERMITIDO
                return true;
            }

            // PRIORIDAD 2: Si NO tiene suscripción de pago, verificar licencia de prueba
            // La licencia de prueba es temporal (7 días) y solo se usa si NO hay suscripción de pago
            boolean licenciaValida = licenciaService.tieneLicenciaValida();

            if (!licenciaValida) {
                // Sin suscripción de pago Y sin licencia de prueba válida
                // Redirigir a /planes para que el usuario:
                // 1. Adquiera una suscripción de pago (RECOMENDADO), o
                // 2. Inicie su prueba gratuita si aún no lo ha hecho
                response.sendRedirect("/planes?expired=true");
                return false;
            }

            // Tiene licencia de prueba válida (y no tiene suscripción de pago)
            // Permitir acceso temporal
            return true;

        } catch (Exception e) {
            // Si hay cualquier error (tabla no existe, MAC address no se puede obtener, etc.)
            // PERMITIR ACCESO y registrar el error
            System.err.println("ERROR en LicenciaInterceptor: " + e.getMessage());
            e.printStackTrace();

            // PERMITIR ACCESO - No bloquear la aplicación por errores de licencia
            return true;
        }
    }

    /**
     * Verifica si la ruta es pública y no requiere validación de licencia
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/css/") ||
               requestURI.startsWith("/js/") ||
               requestURI.startsWith("/images/") ||
               requestURI.startsWith("/static/") ||
               requestURI.startsWith("/webjars/") ||
               requestURI.equals("/") ||
               requestURI.equals("/login") ||
               requestURI.equals("/registro") ||
               requestURI.equals("/planes") ||
               requestURI.equals("/oauth2/") ||
               requestURI.startsWith("/oauth2/") ||
               requestURI.equals("/error") ||
               requestURI.startsWith("/api/licencia/");
    }
}
