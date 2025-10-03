package com.mpmt.backend.repository;

import com.mpmt.backend.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskId(Long taskId);
    List<TaskHistory> findByChangedBy(Long userId);
}
