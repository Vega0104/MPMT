// src/test/java/com/mpmt/backend/security/ProjectSecurityTest.java
package com.mpmt.backend.security;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.service.ProjectMemberService;
import com.mpmt.backend.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectSecurityTest {

    @Mock ProjectService projectService;
    @Mock ProjectMemberService projectMemberService;

    @InjectMocks ProjectSecurity projectSecurity;

    private TestingAuthenticationToken authUser(long id, String... roles) {
        User u = new User(); u.setId(id);
        return new TestingAuthenticationToken(u, null, roles);
    }

    // 1) Admin global ⇒ true (même si projet absent)
    @Test
    void canDelete_adminRole_true() {
        assertTrue(projectSecurity.canDeleteProject(1L, new TestingAuthenticationToken("x", null, "ROLE_ADMIN")));
        verifyNoInteractions(projectService, projectMemberService);
    }

    // 2) Auth null ⇒ false
    @Test
    void canDelete_nullAuth_false() {
        assertFalse(projectSecurity.canDeleteProject(1L, null));
    }

    // 3) Projet introuvable (non-admin) ⇒ false
    @Test
    void canDelete_projectNotFound_false() {
        when(projectService.getProjectById(1L)).thenReturn(Optional.empty());
        assertFalse(projectSecurity.canDeleteProject(1L, authUser(10L, "ROLE_MEMBER")));
    }

    // 4) Créateur du projet ⇒ true
    @Test
    void canDelete_creator_true() {
        Project p = new Project(); p.setCreatedBy(10L);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p));

        assertTrue(projectSecurity.canDeleteProject(1L, authUser(10L, "ROLE_MEMBER")));
        verify(projectMemberService, never()).findMembersByProjectId(anyLong());
    }

    // 5) Membre ADMIN du projet ⇒ true
    @Test
    void canDelete_projectAdminMember_true() {
        Project p = new Project(); p.setCreatedBy(99L);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p));

        ProjectMember pm = new ProjectMember();
        pm.setUserId(20L); pm.setRole(RoleType.ADMIN);
        when(projectMemberService.findMembersByProjectId(1L)).thenReturn(List.of(pm));

        assertTrue(projectSecurity.canDeleteProject(1L, authUser(20L, "ROLE_MEMBER")));
    }

    // 6) Membre non-admin ⇒ false
    @Test
    void canDelete_memberNotAdmin_false() {
        Project p = new Project(); p.setCreatedBy(99L);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p));

        ProjectMember pm = new ProjectMember();
        pm.setUserId(21L); pm.setRole(RoleType.MEMBER);
        when(projectMemberService.findMembersByProjectId(1L)).thenReturn(List.of(pm));

        assertFalse(projectSecurity.canDeleteProject(1L, authUser(21L, "ROLE_MEMBER")));
    }

    // 7) Principal non-User (pas d’ID) ⇒ false (si pas admin, pas créateur, pas admin membre)
    @Test
    void canDelete_principalNotUser_false() {
        Project p = new Project(); p.setCreatedBy(50L);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p));
        when(projectMemberService.findMembersByProjectId(1L)).thenReturn(List.of());

        TestingAuthenticationToken auth = new TestingAuthenticationToken("string-principal", null, "ROLE_MEMBER");
        assertFalse(projectSecurity.canDeleteProject(1L, auth));
    }
}
