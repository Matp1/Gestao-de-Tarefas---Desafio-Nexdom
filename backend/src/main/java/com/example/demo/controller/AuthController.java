package com.example.demo.controller;

import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller responsável pela autenticação de usuários.
 * Gerencia o endpoint de login e a geração de tokens JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint para autenticação de usuário.
     * Recebe as credenciais, valida e retorna um token JWT em caso de sucesso.
     * * @param user Mapa contendo 'username' e 'password'.
     * @return Mapa contendo o 'token' gerado.
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        
        // Simulação de autenticação baseada nas credenciais fornecidas: admin / admin123
        if ("admin".equals(user.get("username")) && "admin123".equals(user.get("password"))) {
            
            // Gera o token JWT utilizando a utilidade de segurança
            String token = jwtUtil.generateToken(user.get("username"));
            
            // Retorna o token em formato JSON { "token": "valor" }
            return Map.of("token", token);
        }
        
        // Lança exceção em caso de credenciais incorretas, capturada pelo Spring Security (403/401)
        throw new RuntimeException("Usuário ou senha inválidos");
    }
}