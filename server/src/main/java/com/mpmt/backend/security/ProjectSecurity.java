// java
package com.mpmt.backend.security;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProjectSecurity {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectSecurity(ProjectService projectService, ProjectMemberService projectMemberService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
    }

    public boolean canDeleteProject(Long projectId, Authentication authentication) {
        if (authentication == null) return false;

        // ROLE_ADMIN bypass
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(a.getAuthority())) return true;
        }

        Object principal = authentication.getPrincipal();
        Long userId = null;
        if (principal instanceof User u) {
            userId = u.getId();
        }

        // If project not found -> deny (caller may handle not found separately)
        Optional<Project> opt = projectService.getProjectById(projectId);
        if (opt.isEmpty()) return false;
        Project project = opt.get();

        // Creator can delete
        if (userId != null && project.getCreatedBy() != null && project.getCreatedBy().equals(userId)) {
            return true;
        }

        // Project member with ADMIN role can delete
        List<ProjectMember> members = projectMemberService.findMembersByProjectId(projectId);
        if (userId != null) {
            for (ProjectMember pm : members) {
                if (userId.equals(pm.getUserId()) && pm.getRole() == RoleType.ADMIN) {
                    return true;
                }
            }
        }

        return false;
    }
}
