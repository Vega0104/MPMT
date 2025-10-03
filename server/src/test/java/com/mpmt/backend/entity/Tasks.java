package com.mpmt.backend.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

class TaskTest {

    @Test
    void testTaskGettersAndSetters() {
        Task task = new Task();
        task.setId(1L);
        task.setName("Tâche test");
        task.setDescription("Desc test");
        task.setDueDate(LocalDate.of(2025, 7, 3));
        task.setEndDate(LocalDate.of(2025, 7, 4));
        task.setPriority("HIGH");
        task.setStatus("TODO");
        task.setCreatedBy(2L);
        task.setProjectId(3L);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getName()).isEqualTo("Tâche test");
        assertThat(task.getDescription()).isEqualTo("Desc test");
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2025, 7, 3));
        assertThat(task.getEndDate()).isEqualTo(LocalDate.of(2025, 7, 4));
        assertThat(task.getPriority()).isEqualTo("HIGH");
        assertThat(task.getStatus()).isEqualTo("TODO");
        assertThat(task.getCreatedBy()).isEqualTo(2L);
        assertThat(task.getProjectId()).isEqualTo(3L);
    }
}
