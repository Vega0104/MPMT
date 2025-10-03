package com.mpmt.backend.controller;

import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.service.TaskAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/task-assignments")
@CrossOrigin(origins = "*")
public class TaskAssignmentController {

    private final TaskAssignmentService service;

    @Autowired
    public TaskAssignmentController(TaskAssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskAssignment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskAssignment> getById(@PathVariable Long id) {
        Optional<TaskAssignment> found = service.getById(id);
        return found.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-task/{taskId}")
    public List<TaskAssignment> getByTaskId(@PathVariable Long taskId) {
        return service.getByTaskId(taskId);
    }

    @GetMapping("/by-project-member/{projectMemberId}")
    public List<TaskAssignment> getByProjectMemberId(@PathVariable Long projectMemberId) {
        return service.getByProjectMemberId(projectMemberId);
    }

    @GetMapping("/by-task-and-member")
    public ResponseEntity<TaskAssignment> getByTaskAndMember(
            @RequestParam Long taskId,
            @RequestParam Long projectMemberId
    ) {
        TaskAssignment found = service.getByTaskIdAndProjectMemberId(taskId, projectMemberId);
        return (found != null) ? ResponseEntity.ok(found) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskAssignment> create(@RequestBody TaskAssignment assignment) {
        return ResponseEntity.ok(service.create(assignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
