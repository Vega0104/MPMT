package com.mpmt.backend.repository;

import com.mpmt.backend.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest2 {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Devrait sauvegarder un projet et générer un ID")
    void shouldSaveProjectAndGenerateId() {
        // 1. Création d’une date précise : 2 juillet 2025
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 2, 0, 0, 0);
        Date startDate = cal.getTime();
        Date createdAt = new Date();

        // 2. Création du projet
        Project project = new Project();
        project.setName("Projet Test");
        project.setDescription("Projet de test unitaire");
        project.setStartDate(startDate);
        project.setCreatedAt(createdAt);

        // 3. Sauvegarde
        Project saved = projectRepository.save(project);

        // 4. Vérifications
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Projet Test");
        assertThat(saved.getDescription()).isEqualTo("Projet de test unitaire");
        assertThat(saved.getStartDate()).isEqualTo(startDate);
        assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
    }
}
