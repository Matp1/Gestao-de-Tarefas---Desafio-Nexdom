package com.example.demo.controller;

import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        // Simulação de login: admin / admin123
        if ("admin".equals(user.get("username")) && "admin123".equals(user.get("password"))) {
            String token = jwtUtil.generateToken(user.get("username"));
            return Map.of("token", token);
        }
        throw new RuntimeException("Usuário ou senha inválidos");
    }
}