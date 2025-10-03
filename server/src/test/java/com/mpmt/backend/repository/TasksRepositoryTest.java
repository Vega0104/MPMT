package com.mpmt.backend.repository;

import com.mpmt.backend.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndFindTaskById() {
        Task task = new Task();
        task.setName("Ma tâche");
        task.setDescription("Desc");
        task.setDueDate(LocalDate.now());
        task.setEndDate(LocalDate.now().plusDays(1));
        task.setPriority("HIGH");
        task.setStatus("TODO");
        task.setCreatedBy(1L);
        task.setProjectId(1L);

        Task saved = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ma tâche");
    }
}
