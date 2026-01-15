package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestão de tarefas.
 * Define os endpoints para operações de CRUD (Create, Read, Update, Delete).
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Retorna a lista completa de tarefas cadastradas no sistema.
     * @return List de objetos Task.
     */
    @GetMapping
    public List<Task> getAll() {
        return taskService.getAllTasks();
    }

    /**
     * Cria uma nova tarefa.
     * @param task Objeto Task recebido no corpo da requisição (JSON).
     * @return O objeto Task persistido, incluindo id e metadados.
     */
    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskService.saveTask(task);
    }

    /**
     * Atualiza os dados de uma tarefa existente com base no ID.
     * @param id Identificador único da tarefa.
     * @param task Objeto Task com as novas informações.
     * @return O objeto Task atualizado.
     */
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    /**
     * Remove uma tarefa do sistema permanentemente.
     * @param id Identificador único da tarefa a ser excluída.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}