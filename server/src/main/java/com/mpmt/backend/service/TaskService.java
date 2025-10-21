package com.mpmt.backend.service;

import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProject_Id(projectId);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Met à jour UNE tâche sans jamais toucher à createdBy ni à project.
     * On charge l'entité existante par son id (fourni dans 'task') puis on copie seulement
     * les champs modifiables.
     */
    @Transactional
    public Task updateTask(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task id must be provided for update");
        }

        Task existing = taskRepository.findById(task.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + task.getId()));

        // ---- Mettre à jour uniquement les champs modifiables ----
        if (task.getName() != null) {
            existing.setName(task.getName());
        }
        if (task.getDescription() != null) {
            existing.setDescription(task.getDescription());
        }
        if (task.getPriority() != null) {
            existing.setPriority(task.getPriority());
        }
        if (task.getStatus() != null) {
            existing.setStatus(task.getStatus());
        }
        // LocalDate: si Jackson reçoit "" côté front, il transformera en null (selon config).
        // Ici, on respecte la valeur désérialisée (null = effacer la date).
        if (task.getDueDate() != null || isExplicitNull(task.getDueDate())) {
            existing.setDueDate(task.getDueDate());
        }
        if (task.getEndDate() != null || isExplicitNull(task.getEndDate())) {
            existing.setEndDate(task.getEndDate());
        }

        // ⚠️ Ne pas toucher à existing.setCreatedBy(...)
        // ⚠️ Ne pas toucher à existing.setProject(...)

        return taskRepository.save(existing);
    }

    /**
     * Helper sémantique: permet de documenter qu'on accepte le null "volontaire".
     * Ici ça renvoie toujours true si l'argument est null, c'est surtout pour la lisibilité.
     */
    private boolean isExplicitNull(Object value) {
        return value == null;
    }

    public List<Task> getTasksByProjectIdAndStatus(Long projectId, StatusType status) {
        return taskRepository.findByProject_IdAndStatus(projectId, status);
    }

    @Transactional
    public Task updateTaskStatus(Long id, StatusType newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));

        if (task.getStatus() == newStatus) {
            return task;
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }
}
