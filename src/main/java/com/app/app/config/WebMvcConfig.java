package com.app.app.config;

import com.app.app.interceptor.LicenciaInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LicenciaInterceptor licenciaInterceptor;

    @Value("${app.licencias.habilitado:false}")
    private boolean licenciasHabilitado;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Solo registrar el interceptor si el sistema de licencias está habilitado
        if (licenciasHabilitado) {
            System.out.println("✅ Sistema de Licencias HABILITADO");
            registry.addInterceptor(licenciaInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/static/**",
                        "/webjars/**",
                        "/",
                        "/login",
                        "/registro",
                        "/planes",
                        "/oauth2/**",
                        "/error",
                        "/licencia/**",
                        "/suscripcion/**"
                    );
        } else {
            System.out.println("⚠️ Sistema de Licencias DESHABILITADO - La aplicación funcionará sin restricciones");
        }
    }
}
