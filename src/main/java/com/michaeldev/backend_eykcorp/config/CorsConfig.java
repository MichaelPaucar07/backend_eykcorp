package com.michaeldev.backend_eykcorp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CONFIGURACIÓN DE CORS PARA PERMITIR QUE EL FRONTEND CONSUMA LA API
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // ORÍGENES PERMITIDOS, DEFINIDOS POR VARIABLE DE ENTORNO (SEPARADOS POR COMA)
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/clientes/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location")
                .maxAge(3600);
    }
}
