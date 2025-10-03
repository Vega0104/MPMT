package com.mpmt.backend.service;

import com.mpmt.backend.entity.TaskHistory;
import com.mpmt.backend.repository.TaskHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskHistoryServiceTest {

    private TaskHistoryRepository repository;
    private TaskHistoryService service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskHistoryRepository.class);
        service = new TaskHistoryService(repository);
    }

    @Test
    @DisplayName("Should create and retrieve task history")
    void shouldCreateAndRetrieveTaskHistory() {
        TaskHistory history = new TaskHistory();
        history.setId(1L);
        history.setTaskId(1L);
        history.setChangedBy(2L);
        history.setChangeDate(new Date());
        history.setChangeDescription("Task updated");

        when(repository.save(any(TaskHistory.class))).thenReturn(history);
        when(repository.findById(1L)).thenReturn(Optional.of(history));

        TaskHistory saved = service.createHistory(history);
        Optional<TaskHistory> found = service.getHistoryById(1L);

        assertThat(saved.getChangeDescription()).isEqualTo("Task updated");
        assertThat(found).isPresent();
        assertThat(found.get().getTaskId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get histories by taskId")
    void shouldGetHistoriesByTaskId() {
        TaskHistory history = new TaskHistory();
        history.setTaskId(1L);
        history.setChangedBy(2L);
        history.setChangeDate(new Date());
        history.setChangeDescription("Task updated");

        when(repository.findByTaskId(1L)).thenReturn(Arrays.asList(history));

        List<TaskHistory> histories = service.getHistoriesByTaskId(1L);
        assertThat(histories).isNotEmpty();
    }
}
