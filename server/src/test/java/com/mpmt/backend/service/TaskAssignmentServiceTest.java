package com.mpmt.backend.service;

import com.mpmt.backend.entity.TaskAssignment;
import com.mpmt.backend.repository.TaskAssignmentRepository;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class TaskAssignmentServiceTest {

    private TaskAssignmentRepository taskAssignmentRepository;
    private ProjectMemberRepository projectMemberRepository;
    private TaskRepository taskRepository;
    private TaskAssignmentService service;

    @BeforeEach
    void setUp() {
        // Mock ALL required repositories
        taskAssignmentRepository = Mockito.mock(TaskAssignmentRepository.class);
        projectMemberRepository = Mockito.mock(ProjectMemberRepository.class);
        taskRepository = Mockito.mock(TaskRepository.class);

        // Pass all mocks to the service constructor
        service = new TaskAssignmentService(
                taskAssignmentRepository,
                projectMemberRepository,
                taskRepository
        );
    }

    @Test
    void shouldReturnAssignmentById() {
        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(99L);
        given(taskAssignmentRepository.findById(99L)).willReturn(Optional.of(assignment));

        Optional<TaskAssignment> found = service.getById(99L);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(99L);
    }
}
