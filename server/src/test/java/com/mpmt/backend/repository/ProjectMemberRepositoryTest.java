package com.mpmt.backend.repository;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.mpmt.backend.entity.RoleType;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectMemberRepositoryTest {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Devrait sauvegarder et retrouver un ProjectMember")
    void shouldSaveAndFindProjectMember() {
        // Création des dépendances : User et Project
        User user = new User();
        user.setUsername("member1");
        user.setEmail("member1@example.com");
        user.setPassword("securepwd");
        user = userRepository.save(user);

        Project project = new Project();
        project.setName("Projet TDD");
        project.setDescription("Pour le test des membres");
        project = projectRepository.save(project);

        // Création du ProjectMember
        ProjectMember pm = new ProjectMember();
        pm.setUser(user);
        pm.setProject(project);
        pm.setRole(RoleType.MEMBER);
        projectMemberRepository.save(pm);

        // Test : retrouver le ProjectMember par user et project
        Optional<ProjectMember> found = projectMemberRepository.findByUserAndProject(user, project);
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(com.mpmt.backend.entity.RoleType.MEMBER);
    }
}
