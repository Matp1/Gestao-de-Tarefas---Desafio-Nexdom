package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data // Gera automaticamente Getters, Setters e o construtor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Data em que a tarefa foi criada (automático)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Prazo de conclusão definido pelo usuário (Apenas data YYYY-MM-DD)
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status; // Ex: PENDENTE, CONCLUIDA

    // Este método roda automaticamente antes de salvar no banco pela primeira vez
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDENTE";
        }
    }
}