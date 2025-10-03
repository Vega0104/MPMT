package com.mpmt.backend.repository;

import com.mpmt.backend.entity.TaskHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskHistoryRepositoryTest {

    @Autowired
    private TaskHistoryRepository repository;

    @Test
    @DisplayName("Should save and retrieve task history by taskId")
    void shouldSaveAndFindByTaskId() {
        TaskHistory history = new TaskHistory();
        history.setTaskId(1L);
        history.setChangedBy(2L);
        history.setChangeDate(new Date());
        history.setChangeDescription("Task created");

        repository.save(history);

        List<TaskHistory> found = repository.findByTaskId(1L);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getChangeDescription()).isEqualTo("Task created");
    }
}
