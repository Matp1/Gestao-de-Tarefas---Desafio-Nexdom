package com.example.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Classe utilitária para gerenciamento de JSON Web Tokens (JWT).
 * Responsável pela geração, assinatura e extração de dados dos tokens de acesso.
 */
@Component
public class JwtUtil {

    // Gera uma chave segura e aleatória para assinatura HS256 em tempo de execução
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Tempo de expiração do token definido em milissegundos (86.400.000 ms = 24 horas)
    private final int expirationTime = 86400000;

    /**
     * Gera um novo token JWT para um usuário autenticado.
     * @param username Nome do usuário (Subject) a ser encapsulado no token.
     * @return String contendo o JWT compacto e assinado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Define o dono do token
                .setIssuedAt(new Date()) // Define a data de emissão
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Define o prazo de validade
                .signWith(key) // Assina o token com a chave secreta HS256
                .compact(); // Constrói a string final separada por pontos
    }

    /**
     * Extrai o nome de usuário (Subject) de um token JWT recebido.
     * Este processo valida automaticamente a assinatura do token antes da leitura.
     * @param token String do token JWT.
     * @return O nome do usuário extraído do corpo (Claims) do token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Define a chave para validar a assinatura
                .build()
                .parseClaimsJws(token) // Faz o parse e valida a integridade do token
                .getBody()
                .getSubject(); // Recupera o campo 'sub' (subject)
    }
}