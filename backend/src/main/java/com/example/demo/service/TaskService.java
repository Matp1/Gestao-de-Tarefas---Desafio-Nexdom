package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    // Método para integrar com API externa
    public String getExternalSuggestion() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return (String) response.get("body");
    }

    public Task saveTask(Task task) {
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            task.setDescription("Sugestão externa: " + getExternalSuggestion());
        }
        return repository.save(task);
    }
}