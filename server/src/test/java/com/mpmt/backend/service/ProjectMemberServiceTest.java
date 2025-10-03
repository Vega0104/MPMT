package com.mpmt.backend.service;

import com.mpmt.backend.entity.*;
import com.mpmt.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectMemberServiceTest {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Devrait sauvegarder et retrouver un ProjectMember")
    void shouldSaveAndFindProjectMember() {
        // Setup User
        User user = new User();
        user.setUsername("pmuser");
        user.setEmail("pmuser@test.com");
        user.setPassword("secret");
        user = userRepository.save(user);

        // Setup Project
        Project project = new Project();
        project.setName("Projet PM");
        project.setDescription("Pour test ProjectMember");
        project = projectRepository.save(project);

        // Service "manuel" pour test TDD
        ProjectMemberService service = new ProjectMemberService(projectMemberRepository);

        // Création ProjectMember
        ProjectMember pm = new ProjectMember();
        pm.setUser(user);
        pm.setProject(project);
        pm.setRole(RoleType.MEMBER);

        ProjectMember saved = service.createProjectMember(pm);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getUsername()).isEqualTo("pmuser");
        assertThat(saved.getProject().getName()).isEqualTo("Projet PM");

        // Vérif via repo/service
        assertThat(service.getById(saved.getId())).isPresent();
    }
}
