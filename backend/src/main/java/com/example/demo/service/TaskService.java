package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

/**
 * Classe de serviço responsável pela lógica de negócio das tarefas.
 * Centraliza as operações de manipulação de dados e integrações externas.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    /**
     * Consome uma API externa (JSONPlaceholder) para obter uma sugestão de conteúdo.
     * Demonstrando capacidade de integração entre sistemas.
     * @return String contendo a sugestão ou um fallback em caso de erro.
     */
    public String getExternalSuggestion() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://jsonplaceholder.typicode.com/posts/1";
            
            // Realiza a chamada GET e mapeia a resposta para um Map
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return (String) response.get("body");
        } catch (Exception e) {
            // Mecanismo de Fallback: Caso a API externa esteja offline, o sistema não trava
            return "Realizar tarefas pendentes com foco."; 
        }
    }

    /**
     * Recupera todas as tarefas do banco de dados através do repositório.
     * @return List de Task.
     */
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    /**
     * Regra de Negócio: Salva uma nova tarefa. 
     * Caso a descrição esteja vazia, utiliza a integração externa para preenchê-la.
     * @param task Objeto vindo do controller.
     * @return Task persistida com os dados processados.
     */
    public Task saveTask(Task task) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            task.setDescription("Sugestão externa: " + getExternalSuggestion());
        }
        return repository.save(task);
    }

    /**
     * Atualiza os dados de uma tarefa existente.
     * Valida a existência do ID antes de proceder com a alteração.
     * @param id Identificador da tarefa.
     * @param taskDetails Novos dados da tarefa.
     * @return Task atualizada.
     */
    public Task updateTask(Long id, Task taskDetails) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        // Atualização seletiva dos campos
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        
        // Mantém a flexibilidade para atualizar a data de vencimento se presente
        if (taskDetails.getDueDate() != null) {
            task.setDueDate(taskDetails.getDueDate());
        }

        return repository.save(task);
    }

    /**
     * Remove o registro da tarefa do banco de dados por ID.
     * @param id Identificador da tarefa.
     */
    public void deleteTask(Long id) {
        repository.deleteById(id);
    }
}