package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuração central de segurança do Spring Security.
 * Define as regras de acesso, política de sessão, filtros de autenticação e CORS.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Define a cadeia de filtros de segurança (Security Filter Chain).
     * Configura quais rotas são públicas, quais são protegidas e como o JWT é processado.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Ativa a configuração de CORS definida no Bean abaixo
            .cors(Customizer.withDefaults()) 
            
            // Desabilita CSRF (Cross-Site Request Forgery), pois a autenticação via Token é Stateless
            .csrf(csrf -> csrf.disable())
            
            // Define a política de criação de sessão como Stateless (não armazena estado no servidor)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Regras de autorização de requisições
            .authorizeHttpRequests(auth -> auth
                // Permite todas as requisições de pre-flight (OPTIONS) do navegador devido ao CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Libera os endpoints de autenticação (com e sem prefixo /api) para acesso público
                .requestMatchers("/api/auth/**").permitAll() 
                .requestMatchers("/auth/**").permitAll()
                
                // Qualquer outra requisição exige que o usuário esteja autenticado via JWT
                .anyRequest().authenticated()
            )
            
            // Insere o filtro customizado de JWT antes do filtro de autenticação padrão do Spring
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura o CorsFilter como um Bean de alta prioridade.
     * Define quais origens, métodos e cabeçalhos o backend aceita de clientes externos.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite o envio de credenciais (como headers de Authorization)
        config.setAllowCredentials(true);
        
        // Lista de URLs permitidas (Ambientes de desenvolvimento local e container Docker)
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://localhost:5174", 
            "http://localhost"
        ));
        
        // Cabeçalhos HTTP permitidos nas requisições
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        
        // Métodos HTTP permitidos para interação com a API
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Aplica essa configuração para todos os caminhos (endpoints) do servidor
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}