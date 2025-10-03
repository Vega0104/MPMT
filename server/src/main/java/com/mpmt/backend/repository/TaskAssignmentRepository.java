package com.mpmt.backend.repository;

import com.mpmt.backend.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByTaskId(Long taskId);
    List<TaskAssignment> findByProjectMemberId(Long projectMemberId);
    TaskAssignment findByTaskIdAndProjectMemberId(Long taskId, Long projectMemberId);
    List<TaskAssignment> findByProjectMemberIdIn(List<Long> projectMemberIds);
}
