package com.mpmt.backend.controller;

import com.mpmt.backend.DTO.ProjectStats;
import com.mpmt.backend.DTO.CreateProjectRequest;
import com.mpmt.backend.DTO.ProjectSummary;
import com.mpmt.backend.entity.*;
import com.mpmt.backend.service.ProjectService;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.TaskService;
import com.mpmt.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public ProjectController(
            ProjectService projectService,
            ProjectMemberService projectMemberService,
            TaskService taskService,
            UserService userService
    ) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.taskService = taskService;
        this.userService = userService;
    }

//    @GetMapping
//    public List<Project> getAllProjects() {
//        return projectService.getAllProjects();
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().<Project>build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        return projectService.getProjectByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProjectSummary> getAllProjects() {
        return projectService.getAllProjects().stream()
                .map(p -> new ProjectSummary(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getStartDate(),
                        p.getCreatedAt()
                ))
                .toList();
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<Object> createProject(
            @RequestBody @Valid CreateProjectRequest request,
            Authentication authentication
    ) {
        // Créer l'entité Project
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());

        // Sauvegarder le projet
        Project createdProject = projectService.createProject(project);

        // Récupérer l'utilisateur authentifié depuis le SecurityContext
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User creator)) {
            throw new RuntimeException("Authenticated principal is not a User");
        }

        // Ajouter le créateur comme admin du projet
        ProjectMember adminMember = new ProjectMember();
        adminMember.setProjectId(createdProject.getId());
        adminMember.setUserId(creator.getId());
        adminMember.setRole(RoleType.ADMIN);
        adminMember.setJoinedAt(new Date());

        projectMemberService.createMember(adminMember);

        return ResponseEntity.ok(createdProject);
    }

    @PreAuthorize("@projectSecurity.canDeleteProject(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectService.getProjectById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMember>> getMembersForProject(@PathVariable Long id) {
        List<ProjectMember> members = projectMemberService.findMembersByProjectId(id);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> getTasksForProject(@PathVariable Long id) {
        List<Task> tasks = taskService.getTasksByProjectId(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ProjectStats> getProjectStats(@PathVariable Long id) {
        List<Task> tasks = taskService.getTasksByProjectId(id);

        if (tasks.isEmpty()) {
            ProjectStats emptyStats = new ProjectStats();
            emptyStats.setTotalTasks(0);
            emptyStats.setProgress(0);
            return ResponseEntity.ok(emptyStats);
        }

        long todo = tasks.stream().filter(t -> t.getStatus() == StatusType.TODO).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == StatusType.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == StatusType.DONE).count();

        ProjectStats stats = new ProjectStats();
        stats.setTotalTasks(tasks.size());
        stats.setTodoCount(todo);
        stats.setInProgressCount(inProgress);
        stats.setDoneCount(done);
        stats.setProgress((int) ((done * 100.0) / tasks.size()));

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Task> createTaskForProject(@PathVariable Long id, @RequestBody Task task) {
        if (projectService.getProjectById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        task.setProjectId(id);
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updatedProject) {
        Optional<Project> optionalProject = projectService.getProjectById(id);
        if (optionalProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Project existingProject = optionalProject.get();
        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setStartDate(updatedProject.getStartDate());

        Project savedProject = projectService.updateProject(existingProject);
        return ResponseEntity.ok(savedProject);
    }
}




//package com.mpmt.backend.controller;
//
//import com.mpmt.backend.DTO.ProjectStats;
//import com.mpmt.backend.DTO.CreateProjectRequest;
//import com.mpmt.backend.DTO.ProjectSummary;
//import com.mpmt.backend.entity.*;
//import com.mpmt.backend.service.ProjectService;
//import com.mpmt.backend.service.ProjectMemberService;
//import com.mpmt.backend.service.TaskService;
//import com.mpmt.backend.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/projects")
//public class ProjectController {
//
//    private final ProjectService projectService;
//    private final ProjectMemberService projectMemberService;
//    private final TaskService taskService;
//    private final UserService userService;
//
//    @Autowired
//    public ProjectController(
//            ProjectService projectService,
//            ProjectMemberService projectMemberService,
//            TaskService taskService,
//            UserService userService
//    ) {
//        this.projectService = projectService;
//        this.projectMemberService = projectMemberService;
//        this.taskService = taskService;
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public List<ProjectSummary> getAllProjects() {
//        return projectService.getAllProjects().stream()
//                .map(p -> new ProjectSummary(
//                        p.getId(),
//                        p.getName(),
//                        p.getDescription(),
//                        p.getStartDate(),
//                        p.getCreatedAt()
//                ))
//                .toList();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
//        return projectService.getProjectById(id)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().<Project>build());
//    }
//
//    @GetMapping("/by-name/{name}")
//    public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
//        return projectService.getProjectByName(name)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // IMPORTANT : on ne vérifie que "isAuthenticated()" car ton JwtAuthFilter met des authorities null.
//    @PreAuthorize("isAuthenticated()")
//    @PostMapping
//    public ResponseEntity<Object> createProject(
//            @RequestBody @Valid CreateProjectRequest request,
//            Authentication authentication
//    ) {
//        // 1) Créer et sauvegarder le projet
//        Project project = new Project();
//        project.setName(request.getName());
//        project.setDescription(request.getDescription());
//        project.setStartDate(request.getStartDate());
//
//        Project createdProject = projectService.createProject(project);
//
//        // 2) Résoudre l'utilisateur authentifié (id)
//        Long creatorId;
//        Object principal = authentication != null ? authentication.getPrincipal() : null;
//
//        if (principal instanceof User u) {
//            creatorId = u.getId();
//        } else {
//            // fallback: username dans auth.getName() => retrouver l'entité User
//            String username = authentication.getName();
//            creatorId = userService.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User not found: " + username))
//                    .getId();
//        }
//
//        // 3) Ajouter le créateur comme ADMIN (idempotent)
//        projectMemberService.addIfAbsent(creatorId, createdProject.getId(), RoleType.ADMIN, new Date());
//
//        return ResponseEntity.ok(createdProject);
//    }
//
//    @PreAuthorize("@projectSecurity.canDeleteProject(#id, authentication)")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
//        if (projectService.getProjectById(id).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        projectService.deleteProject(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{id}/members")
//    public ResponseEntity<List<ProjectMember>> getMembersForProject(@PathVariable Long id) {
//        List<ProjectMember> members = projectMemberService.findMembersByProjectId(id);
//        return ResponseEntity.ok(members);
//    }
//
//    @GetMapping("/{id}/tasks")
//    public ResponseEntity<List<Task>> getTasksForProject(@PathVariable Long id) {
//        List<Task> tasks = taskService.getTasksByProjectId(id);
//        return ResponseEntity.ok(tasks);
//    }
//
//    @GetMapping("/{id}/stats")
//    public ResponseEntity<ProjectStats> getProjectStats(@PathVariable Long id) {
//        List<Task> tasks = taskService.getTasksByProjectId(id);
//
//        if (tasks.isEmpty()) {
//            ProjectStats emptyStats = new ProjectStats();
//            emptyStats.setTotalTasks(0);
//            emptyStats.setProgress(0);
//            return ResponseEntity.ok(emptyStats);
//        }
//
//        long todo = tasks.stream().filter(t -> t.getStatus() == StatusType.TODO).count();
//        long inProgress = tasks.stream().filter(t -> t.getStatus() == StatusType.IN_PROGRESS).count();
//        long done = tasks.stream().filter(t -> t.getStatus() == StatusType.DONE).count();
//
//        ProjectStats stats = new ProjectStats();
//        stats.setTotalTasks(tasks.size());
//        stats.setTodoCount(todo);
//        stats.setInProgressCount(inProgress);
//        stats.setDoneCount(done);
//        stats.setProgress((int) ((done * 100.0) / tasks.size()));
//
//        return ResponseEntity.ok(stats);
//    }
//
//    @PostMapping("/{id}/tasks")
//    public ResponseEntity<Task> createTaskForProject(@PathVariable Long id, @RequestBody Task task) {
//        if (projectService.getProjectById(id).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        task.setProjectId(id);
//        Task created = taskService.createTask(task);
//        return ResponseEntity.ok(created);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updatedProject) {
//        Optional<Project> optionalProject = projectService.getProjectById(id);
//        if (optionalProject.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        Project existingProject = optionalProject.get();
//        existingProject.setName(updatedProject.getName());
//        existingProject.setDescription(updatedProject.getDescription());
//        existingProject.setStartDate(updatedProject.getStartDate());
//
//        Project savedProject = projectService.updateProject(existingProject);
//        return ResponseEntity.ok(savedProject);
//    }
//}
