package com.mpmt.backend.security;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.service.ProjectMemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
public class ProjectSecurity {

    private final ProjectMemberService projectMemberService;

    public ProjectSecurity(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    public boolean canDeleteProject(Long projectId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return false;

        // Le JwtAuthFilter met l'entité User comme principal
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) return false;

        // Retrouver le membership utilisateur-projet et vérifier le rôle ADMIN
        return projectMemberService.findByProjectId(projectId).stream()
                .anyMatch(pm -> isAdminOfProject(pm, user.getId()));
    }

    private boolean isAdminOfProject(ProjectMember pm, Long userId) {
        return pm.getUser() != null
                && pm.getUser().getId().equals(userId)
                && pm.getRole() == RoleType.ADMIN;
    }
}
