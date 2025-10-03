package com.mpmt.backend.repository;

import com.mpmt.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByTaskId(Long taskId);
    List<Notification> findByUserIdAndReadFalse(Long userId);
}
