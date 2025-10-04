package com.mpmt.backend.controller;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project-members")
//@CrossOrigin(origins = "*")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
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

    @PostMapping
    public ResponseEntity<ProjectMember> createProjectMember(@RequestBody ProjectMember member) {
        // Ici tu pourrais ajouter une logique de vérification si déjà membre, etc.
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
            @RequestBody RoleUpdateRequest request // DTO simple
    ) {
        Optional<ProjectMember> updated = projectMemberService.updateRole(id, request.getRole());
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DTO pour désérialiser le body JSON {"role": "ADMIN"}
    public static class RoleUpdateRequest {
        private String role;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

}
