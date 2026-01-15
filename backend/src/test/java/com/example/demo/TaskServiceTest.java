package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveSalvarTarefaComDescricaoAutomaticaSeVazio() {
        // Arrange (Preparação)
        Task taskInput = new Task();
        taskInput.setTitle("Estudar Testes");
        taskInput.setDescription(""); // Descrição vazia

        // Simulamos o comportamento do repositório
        when(repository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act (Ação)
        Task savedTask = taskService.saveTask(taskInput);

        // Assert (Verificação)
        assertNotNull(savedTask.getDescription());
        assertTrue(savedTask.getDescription().contains("Sugestão externa"));
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    void deveManterDescricaoSeUsuarioInformar() {
        // Arrange
        Task taskInput = new Task();
        taskInput.setTitle("Tarefa com Descrição");
        taskInput.setDescription("Minha descrição manual");

        when(repository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Task savedTask = taskService.saveTask(taskInput);

        // Assert
        assertEquals("Minha descrição manual", savedTask.getDescription());
    }
}