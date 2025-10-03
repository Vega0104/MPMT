package com.mpmt.backend.controller;

import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.service.TaskHistoryService;
import com.mpmt.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private TaskHistoryService taskHistoryService;


    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // @GetMapping("/{id}/histories")
    // public ResponseEntity<List<TaskHistory>> getHistoriesForTask(@PathVariable Long id) {
        // List<TaskHistory> histories = taskHistoryService.getHistoriesByTaskId(id);
        // return ResponseEntity.ok(histories);
    //}

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskService.getTaskById(id).isPresent()) {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Optional<Task> optionalTask = taskService.getTaskById(id);
        if (optionalTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Task existingTask = optionalTask.get();
        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setEndDate(updatedTask.getEndDate());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setCreatedBy(updatedTask.getCreatedBy());
        existingTask.setProjectId(updatedTask.getProjectId());

        Task savedTask = taskService.updateTask(existingTask);
        return ResponseEntity.ok(savedTask);
    }

}
