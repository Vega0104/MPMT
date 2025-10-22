package com.mpmt.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberTest {

    @Test
    @DisplayName("Devrait setter et getter correctement tous les champs ProjectMember")
    void shouldSetAndGetAllFields() {
        ProjectMember pm = new ProjectMember();

        // Test ID
        pm.setId(10L);
        assertThat(pm.getId()).isEqualTo(10L);

        // Test User
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        pm.setUser(user);
        assertThat(pm.getUser().getUsername()).isEqualTo("testuser");

        // Test Project
        Project project = new Project();
        project.setId(2L);
        project.setName("TestProject");
        pm.setProject(project);
        assertThat(pm.getProject().getName()).isEqualTo("TestProject");

        // Test Role
        pm.setRole(RoleType.ADMIN);
        assertThat(pm.getRole()).isEqualTo(RoleType.ADMIN);

        // Test UserId
        pm.setUserId(100L);
        assertThat(pm.getUserId()).isEqualTo(100L);

        // Test ProjectId
        pm.setProjectId(200L);
        assertThat(pm.getProjectId()).isEqualTo(200L);

        // Test JoinedAt
        Date joinedDate = new Date();
        pm.setJoinedAt(joinedDate);
        assertThat(pm.getJoinedAt()).isEqualTo(joinedDate);
    }

    @Test
    @DisplayName("Devrait tester les valeurs par défaut")
    void shouldTestDefaultValues() {
        ProjectMember pm = new ProjectMember();

        // Vérifier que l'objet est créé correctement
        assertThat(pm).isNotNull();
        assertThat(pm.getId()).isNull();
        assertThat(pm.getUserId()).isNull();
        assertThat(pm.getProjectId()).isNull();
    }

    @Test
    @DisplayName("Devrait tester tous les types de rôles")
    void shouldTestAllRoleTypes() {
        ProjectMember pm = new ProjectMember();

        pm.setRole(RoleType.ADMIN);
        assertThat(pm.getRole()).isEqualTo(RoleType.ADMIN);

        pm.setRole(RoleType.MEMBER);
        assertThat(pm.getRole()).isEqualTo(RoleType.MEMBER);

        pm.setRole(RoleType.OBSERVER);
        assertThat(pm.getRole()).isEqualTo(RoleType.OBSERVER);
    }
}