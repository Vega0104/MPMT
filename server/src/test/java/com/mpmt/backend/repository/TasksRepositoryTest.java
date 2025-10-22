// java
package com.mpmt.backend.repository;

import com.mpmt.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldSaveAndFindTaskById() {
        // 1. Créer d'abord un projet (clé étrangère requise)
        Project project = new Project();
        project.setName("Test Project");
        // convertir LocalDateTime en java.sql.Timestamp (extends java.util.Date)
        project.setStartDate(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        project = projectRepository.save(project);

        // 2. Créer la tâche liée au projet
        Task task = new Task();
        task.setName("Ma tâche");
        task.setDescription("Desc");
        task.setDueDate(LocalDate.now());
        task.setPriority(PriorityType.HIGH);
        task.setStatus(StatusType.TODO);
        task.setCreatedBy(1L);
        task.setProject(project);

        Task saved = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ma tâche");
    }
}
