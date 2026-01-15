package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    // 1. Integração com API externa
    public String getExternalSuggestion() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://jsonplaceholder.typicode.com/posts/1";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return (String) response.get("body");
        } catch (Exception e) {
            return "Realizar tarefas pendentes com foco."; // Fallback caso a API externa caia
        }
    }

    // 2. Listar todas as tarefas
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    // 3. Salvar nova tarefa (CREATE)
    public Task saveTask(Task task) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            task.setDescription("Sugestão externa: " + getExternalSuggestion());
        }
        return repository.save(task);
    }

    // 4. Atualizar tarefa (UPDATE)
    public Task updateTask(Long id, Task taskDetails) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        
        // Se o seu modelo tiver dueDate, atualize também:
        // task.setDueDate(taskDetails.getDueDate());

        return repository.save(task);
    }

    // 5. Deletar tarefa (DELETE)
    public void deleteTask(Long id) {
        repository.deleteById(id);
    }
}