package com.app.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuración de localización para Colombia
 * Configura el formato de números, fechas y moneda en pesos colombianos (COP)
 */
@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // Configurar localización para Colombia
        localeResolver.setDefaultLocale(new Locale("es", "CO"));
        return localeResolver;
    }
}
