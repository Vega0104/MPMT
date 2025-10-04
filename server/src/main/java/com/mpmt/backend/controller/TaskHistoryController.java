package com.mpmt.backend.controller;

import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.service.TaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*")
public class TaskHistoryController {

    private final TaskHistoryService service;

    @Autowired
    public TaskHistoryController(TaskHistoryService service) {
        this.service = service;
    }

    // RESTful: Lister l'historique d'une tâche donnée
    @GetMapping("/tasks/{taskId}/histories")
    public ResponseEntity<List<TaskHistory>> getHistoriesForTask(@PathVariable Long taskId) {
        List<TaskHistory> histories = service.getHistoriesByTaskId(taskId);
        return ResponseEntity.ok(histories);
    }

    // Legacy endpoints pour compatibilité
    @GetMapping("/task-histories")
    public List<TaskHistory> getAllHistories() {
        return service.getAllHistories();
    }

    @GetMapping("/task-histories/{id}")
    public ResponseEntity<TaskHistory> getHistoryById(@PathVariable Long id) {
        return service.getHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task-histories/by-task/{taskId}")
    public List<TaskHistory> getHistoriesByTaskId(@PathVariable Long taskId) {
        return service.getHistoriesByTaskId(taskId);
    }

    @PostMapping("/task-histories")
    public ResponseEntity<TaskHistory> createHistory(@RequestBody TaskHistory taskHistory) {
        return ResponseEntity.ok(service.createHistory(taskHistory));
    }

    @DeleteMapping("/task-histories/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        service.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
