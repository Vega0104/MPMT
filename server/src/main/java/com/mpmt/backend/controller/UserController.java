package com.mpmt.backend.controller;

import com.mpmt.backend.entity.User;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.Notification;
import com.mpmt.backend.entity.Task;
import com.mpmt.backend.service.UserService;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.NotificationService;
import com.mpmt.backend.service.TaskAssignmentService;
import com.mpmt.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final ProjectMemberService projectMemberService;
    private final TaskAssignmentService taskAssignmentService;
    private final NotificationService notificationService;
    private final TaskService taskService;

    @Autowired
    public UserController(
            UserService userService,
            ProjectMemberService projectMemberService,
            TaskAssignmentService taskAssignmentService,
            NotificationService notificationService,
            TaskService taskService
    ) {
        this.userService = userService;
        this.projectMemberService = projectMemberService;
        this.taskAssignmentService = taskAssignmentService;
        this.notificationService = notificationService;
        this.taskService = taskService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ENDPOINTS AVANCÉS ---

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<Project>> getProjectsForUser(@PathVariable Long id) {
        List<Project> projects = projectMemberService.findProjectsByUserId(id);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable Long id) {
        List<Notification> notifications = notificationService.getByUserId(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> getAssignedTasksForUser(@PathVariable Long id) {
        List<Task> tasks = taskAssignmentService.findTasksAssignedToUser(id);
        return ResponseEntity.ok(tasks);
    }
}
