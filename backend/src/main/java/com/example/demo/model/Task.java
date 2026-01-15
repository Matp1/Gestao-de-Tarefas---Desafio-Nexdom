package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Entidade que representa a tabela 'tasks' no banco de dados.
 * Utiliza JPA para o mapeamento objeto-relacional (ORM).
 */
@Entity
@Table(name = "tasks")
@Data // Anotação do Lombok que gera automaticamente Getters, Setters, toString, Equals e HashCode
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título da tarefa - campo obrigatório
    @Column(nullable = false)
    private String title;

    // Descrição detalhada - definida como TEXT para suportar textos longos
    @Column(columnDefinition = "TEXT")
    private String description;

    // Data de criação da tarefa - gerada automaticamente e não editável via aplicação
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Prazo final para conclusão (apenas data: YYYY-MM-DD)
    @Column(name = "due_date")
    private LocalDate dueDate;

    // Status atual da tarefa (ex: PENDENTE, EM_ANDAMENTO, CONCLUIDA)
    @Column(nullable = false)
    private String status;

    /**
     * Método executado automaticamente pelo JPA antes da persistência inicial.
     * Garante a data de criação e um status padrão caso não sejam informados.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDENTE";
        }
    }
}