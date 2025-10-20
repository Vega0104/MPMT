package com.mpmt.backend.service;

import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// imports à ajouter
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

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByProjectIdAndStatus(Long projectId, StatusType status) {
        return taskRepository.findByProject_IdAndStatus(projectId, status);
    }

    @Transactional
    public Task updateTaskStatus(Long id, StatusType newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));

        // (optionnel) court-circuit si identique
        if (task.getStatus() == newStatus) {
            return task;
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }
}
