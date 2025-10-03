package com.mpmt.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {

    @Test
    @DisplayName("Devrait correctement créer un Project et manipuler ses propriétés")
    void shouldCreateAndManipulateProject() {
        Project project = new Project();
        project.setName("Projet Unitaire");
        project.setDescription("Test de l'entité Project");

        // Crée une date de début (ex : 2 juillet 2025)
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 2, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        project.setStartDate(startDate);

        // Date de création personnalisée (optionnel)
        Date now = new Date();
        project.setCreatedAt(now);

        // Assertions sur les propriétés
        assertThat(project.getId()).isNull(); // Non persisté
        assertThat(project.getName()).isEqualTo("Projet Unitaire");
        assertThat(project.getDescription()).isEqualTo("Test de l'entité Project");
        assertThat(project.getStartDate()).isEqualTo(startDate);
        assertThat(project.getCreatedAt()).isEqualTo(now);
    }
}
