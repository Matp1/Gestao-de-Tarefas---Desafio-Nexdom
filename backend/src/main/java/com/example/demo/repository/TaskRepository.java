package com.example.demo.repository;

import com.example.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para a entidade Task.
 * Ao estender JpaRepository, o Spring Data JPA fornece automaticamente 
 * a implementação de todos os métodos de CRUD padrão (save, findAll, deleteById, etc.)
 * sem a necessidade de escrever código SQL ou implementações manuais.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /* O JpaRepository recebe dois parâmetros genéricos:
       1. A entidade que ele gerencia (Task)
       2. O tipo do identificador primário (Long)
    */
}