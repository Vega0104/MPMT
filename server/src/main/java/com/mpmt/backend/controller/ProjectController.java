// java
package com.mpmt.backend.controller;

import com.mpmt.backend.DTO.CreateProjectRequest;
import com.mpmt.backend.DTO.ProjectStats;
import com.mpmt.backend.DTO.ProjectSummary;
import com.mpmt.backend.entity.*;
import com.mpmt.backend.security.ProjectSecurity;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.ProjectService;
import com.mpmt.backend.service.TaskService;
import com.mpmt.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    private final ProjectSecurity projectSecurity;

    @Autowired
    public ProjectController(
            ProjectService projectService,
            ProjectMemberService projectMemberService,
            TaskService taskService,
            UserService userService,
            ProjectSecurity projectSecurity
    ) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.taskService = taskService;
        this.userService = userService;
        this.projectSecurity = projectSecurity;
    }

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

    @PostMapping
    public ResponseEntity<Object> createProject(
            @RequestBody @Valid CreateProjectRequest request,
            Authentication authentication
    ) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());

        Project createdProject = projectService.createProject(project);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User creator)) {
            throw new RuntimeException("Authenticated principal is not a User");
        }

        ProjectMember adminMember = new ProjectMember();
        adminMember.setProjectId(createdProject.getId());
        adminMember.setUserId(creator.getId());
        adminMember.setRole(RoleType.ADMIN);
        adminMember.setJoinedAt(new Date());

        projectMemberService.createMember(adminMember);

        return ResponseEntity.ok(createdProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        if (!projectSecurity.canDeleteProject(id, authentication)) {
            return ResponseEntity.status(403).build();
        }
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
