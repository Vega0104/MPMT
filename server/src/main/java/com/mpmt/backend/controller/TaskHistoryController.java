package com.mpmt.backend.controller;

import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.service.TaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
// @CrossOrigin(origins = "*")
public class TaskHistoryController {

    private final TaskHistoryService service;

    @Autowired
    public TaskHistoryController(TaskHistoryService service) {
        this.service = service;
    }

    /**
     * ✅ Route attendue par le front:
     * GET /api/tasks/{taskId}/history
     * Protégée: accessible si l'utilisateur a accès à la tâche (ADMIN/MEMBER/OBSERVER du projet).
     */
    @GetMapping("/tasks/{taskId}/history")
    @PreAuthorize("@taskSecurity.canAccessTask(#taskId)")
    public List<TaskHistory> getHistoryForTask(@PathVariable("taskId") Long taskId) {
        return service.getHistoriesByTaskId(taskId);
    }

    /**
     * RESTful (ta version initiale, conservée pour compat)
     * GET /api/tasks/{taskId}/histories
     */
    @GetMapping("/tasks/{taskId}/histories")
    @PreAuthorize("@taskSecurity.canAccessTask(#taskId)")
    public ResponseEntity<List<TaskHistory>> getHistoriesForTask(@PathVariable("taskId") Long taskId) {
        List<TaskHistory> histories = service.getHistoriesByTaskId(taskId);
        return ResponseEntity.ok(histories);
    }

    // ------- Legacy endpoints pour compatibilité -------

    // GET /api/task-histories
    @GetMapping("/task-histories")
    @PreAuthorize("isAuthenticated()")
    public List<TaskHistory> getAllHistories() {
        return service.getAllHistories();
    }

    // GET /api/task-histories/{id}
    @GetMapping("/task-histories/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskHistory> getHistoryById(@PathVariable Long id) {
        return service.getHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/task-histories/by-task/{taskId}
    @GetMapping("/task-histories/by-task/{taskId}")
    @PreAuthorize("@taskSecurity.canAccessTask(#taskId)")
    public List<TaskHistory> getHistoriesByTaskId(@PathVariable("taskId") Long taskId) {
        return service.getHistoriesByTaskId(taskId);
    }

    // POST /api/task-histories
    @PostMapping("/task-histories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    public ResponseEntity<TaskHistory> createHistory(@RequestBody TaskHistory taskHistory) {
        return ResponseEntity.ok(service.createHistory(taskHistory));
    }

    // DELETE /api/task-histories/{id}
    @DeleteMapping("/task-histories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        service.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
