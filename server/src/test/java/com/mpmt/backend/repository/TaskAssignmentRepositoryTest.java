package com.mpmt.backend.repository;

import com.mpmt.backend.entity.TaskAssignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskAssignmentRepositoryTest {

    @Autowired
    private TaskAssignmentRepository repository;

    @Test
    @DisplayName("Doit sauvegarder et retrouver un TaskAssignment")
    void shouldSaveAndFindTaskAssignment() {
        TaskAssignment ta = new TaskAssignment();
        ta.setTaskId(10L);
        ta.setProjectMemberId(20L);

        TaskAssignment saved = repository.save(ta);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTaskId()).isEqualTo(10L);
        assertThat(saved.getProjectMemberId()).isEqualTo(20L);

        var found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTaskId()).isEqualTo(10L);
    }
}
