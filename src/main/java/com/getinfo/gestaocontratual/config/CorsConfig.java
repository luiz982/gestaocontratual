package com.getinfo.gestaocontratual.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // Libera para todas as origens
                        .allowedMethods("*")         // Libera todos os métodos HTTP (GET, POST, etc)
                        .allowedHeaders("*")         // Libera todos os cabeçalhos
                        .allowCredentials(true);     // Permite envio de cookies/autenticação
            }
        };
    }
}