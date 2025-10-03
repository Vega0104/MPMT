package com.mpmt.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMemberTest {

    @Test
    @DisplayName("Devrait setter et getter correctement les champs ProjectMember")
    void shouldSetAndGetFields() {
        ProjectMember pm = new ProjectMember();
        pm.setId(10L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Project project = new Project();
        project.setId(2L);
        project.setName("TestProject");

        pm.setRole(RoleType.ADMIN);
        pm.setUser(user);
        pm.setProject(project);

        assertThat(pm.getId()).isEqualTo(10L);
        assertThat(pm.getRole()).isEqualTo(RoleType.ADMIN);
        assertThat(pm.getUser().getUsername()).isEqualTo("testuser");
        assertThat(pm.getProject().getName()).isEqualTo("TestProject");
    }
}
