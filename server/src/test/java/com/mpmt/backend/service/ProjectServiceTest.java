package com.mpmt.backend.service;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectServiceTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Devrait créer, récupérer et supprimer un projet")
    void shouldCreateAndFindAndDeleteProject() {
        // Service simple basé sur le repository
        ProjectService service = new ProjectService(projectRepository);

        // Création d'un projet
        Project project = new Project();
        project.setName("ProjectServiceTest");
        project.setDescription("Desc");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 2, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        project.setStartDate(startDate);
        project.setCreatedAt(new Date());

        Project saved = service.createProject(project);

        assertThat(saved.getId()).isNotNull();

        // Récupération par ID
        Project found = service.getProjectById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("ProjectServiceTest");

        // Suppression
        service.deleteProject(saved.getId());
        assertThat(service.getProjectById(saved.getId())).isNotPresent();
    }
}
