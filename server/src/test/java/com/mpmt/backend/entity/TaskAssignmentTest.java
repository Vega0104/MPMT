package com.mpmt.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskAssignmentTest {

    @Test
    @DisplayName("Devrait créer un objet TaskAssignment avec taskId et projectMemberId")
    void shouldCreateTaskAssignment() {
        TaskAssignment ta = new TaskAssignment();
        ta.setId(1L);
        ta.setTaskId(2L);
        ta.setProjectMemberId(3L);

        assertThat(ta.getId()).isEqualTo(1L);
        assertThat(ta.getTaskId()).isEqualTo(2L);
        assertThat(ta.getProjectMemberId()).isEqualTo(3L);
    }
}
