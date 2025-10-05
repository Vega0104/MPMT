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
//@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> create(@RequestBody CreateAssignmentRequest request) {
        // Vérifier doublon
        TaskAssignment existing = service.getByTaskIdAndProjectMemberId(
                request.getTaskId(),
                request.getProjectMemberId()
        );

        if (existing != null) {
            return ResponseEntity.badRequest().body("User already assigned to this task");
        }

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTaskId(request.getTaskId());
        assignment.setProjectMemberId(request.getProjectMemberId());

        return ResponseEntity.ok(service.create(assignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateAssignmentRequest {
        private Long taskId;
        private Long projectMemberId;

        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        public Long getProjectMemberId() { return projectMemberId; }
        public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }
    }
}
