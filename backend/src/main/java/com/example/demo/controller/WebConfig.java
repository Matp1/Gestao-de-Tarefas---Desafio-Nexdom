package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing).
 * Esta classe permite que o frontend (Vite/Docker) se comunique com o backend 
 * mesmo estando em origens ou portas diferentes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Habilita o mapeamento para todos os endpoints da API
        registry.addMapping("/**") 
                
                // Define as origens permitidas (Ambiente de Desenvolvimento e Docker)
                .allowedOrigins("http://localhost:5173", "http://localhost") 
                
                // Define os métodos HTTP permitidos para as requisições externas
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                
                // Permite todos os headers (necessário para o envio do token Authorization)
                .allowedHeaders("*")
                
                // Permite o envio de cookies e headers de autenticação entre domínios
                .allowCredentials(true);
    }
}