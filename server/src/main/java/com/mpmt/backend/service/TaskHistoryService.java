package com.mpmt.backend.service;

import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.repository.TaskHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskHistoryService {

    private final TaskHistoryRepository repository;

    @Autowired
    public TaskHistoryService(TaskHistoryRepository repository) {
        this.repository = repository;
    }

    public List<TaskHistory> getAllHistories() {
        return repository.findAll();
    }

    public Optional<TaskHistory> getHistoryById(Long id) {
        return repository.findById(id);
    }

    /**
     * Liste tous les historiques liés à une tâche (pour GET /api/tasks/{id}/histories)
     */
    public List<TaskHistory> getHistoriesByTaskId(Long taskId) {
        return repository.findByTaskId(taskId);
    }

    public TaskHistory createHistory(TaskHistory taskHistory) {
        return repository.save(taskHistory);
    }

    public void deleteHistory(Long id) {
        repository.deleteById(id);
    }
}
