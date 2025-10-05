package com.mpmt.backend.controller;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.ProjectRepository;
import com.mpmt.backend.repository.UserRepository;
import com.mpmt.backend.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project-members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectMemberController(
            ProjectMemberService projectMemberService,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectMemberService = projectMemberService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ProjectMember> getAllProjectMembers() {
        return projectMemberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectMember> getProjectMemberById(@PathVariable Long id) {
        Optional<ProjectMember> member = projectMemberService.getById(id);
        return member.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

//    @PostMapping
//    public ResponseEntity<ProjectMember> createProjectMember(@RequestBody CreateMemberRequest request) {
//        Project project = projectRepository.findById(request.getProjectId())
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        ProjectMember member = new ProjectMember();
//        member.setProject(project);
//        member.setUser(user);
//        member.setRole(RoleType.valueOf(request.getRole()));
//
//        ProjectMember saved = projectMemberService.createProjectMember(member);
//        return ResponseEntity.ok(saved);
//    }

    @PostMapping
    public ResponseEntity<ProjectMember> createProjectMember(@RequestBody CreateMemberRequest request) {
        // Vérifier si le membre existe déjà
        Optional<ProjectMember> existing = projectMemberService.getByUserIdAndProjectId(
                request.getUserId(),
                request.getProjectId()
        );

        if (existing.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(RoleType.valueOf(request.getRole()));

        ProjectMember saved = projectMemberService.createProjectMember(member);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectMember(@PathVariable Long id) {
        if (projectMemberService.getById(id).isPresent()) {
            projectMemberService.deleteProjectMember(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ProjectMember> updateRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request
    ) {
        Optional<ProjectMember> updated = projectMemberService.updateRole(id, request.getRole());
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class CreateMemberRequest {
        private Long projectId;
        private Long userId;
        private String role;

        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class RoleUpdateRequest {
        private String role;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}