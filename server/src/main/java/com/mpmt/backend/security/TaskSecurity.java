package com.mpmt.backend.security;

import com.mpmt.backend.entity.Task;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // ⬅️ NEW
import org.springframework.stereotype.Component;

@Component("taskSecurity")
public class TaskSecurity {

    private final TaskRepository taskRepo;
    private final ProjectMemberRepository pmRepo;

    public TaskSecurity(TaskRepository taskRepo, ProjectMemberRepository pmRepo) {
        this.taskRepo = taskRepo;
        this.pmRepo = pmRepo;
    }

    /**
     * ✅ Méthode utilisée par @PreAuthorize("@taskSecurity.canAccessTask(#taskId)")
     * Récupère l'Authentication depuis le SecurityContext.
     */
    public boolean canAccessTask(Long taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return canEditTask(taskId, auth); // on réutilise la logique existante
    }

    /**
     * Logique d'accès existante : ADMIN global OU membre du projet de la tâche.
     */
    public boolean canEditTask(Long taskId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;

        // 1) ADMIN global
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) return true;

        // 2) Récupérer l'userId depuis le principal (User JPA posé par JwtAuthFilter)
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        // 3) Charger la tâche → vérifier appartenance au projet
        Task task = taskRepo.findById(taskId).orElse(null);
        if (task == null || task.getProject() == null || task.getProject().getId() == null) {
            return false;
        }

        Long projectId = task.getProject().getId();
        // Autoriser si l'utilisateur est membre du projet (ADMIN/MEMBER/OBSERVER côté ProjectMember.role)
        return pmRepo.existsByProject_IdAndUser_Id(projectId, userId);
    }

    private Long extractUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof User u) {
            return u.getId();
        }
        return null;
    }
}
