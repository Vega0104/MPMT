package com.mpmt.backend.repository;

import com.mpmt.backend.entity.StatusType;
import com.mpmt.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject_Id(Long projectId);
    List<Task> findByProject_IdAndStatus(Long projectId, StatusType status);
}
