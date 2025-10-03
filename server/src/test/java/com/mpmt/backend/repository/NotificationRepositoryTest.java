package com.mpmt.backend.repository;

import com.mpmt.backend.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("Should save and find notification by userId")
    void shouldSaveAndFindByUserId() {
        Notification notif = new Notification();
        notif.setUserId(1L);
        notif.setTaskId(2L);
        notif.setContent("Test notification");
        notif.setRead(false);
        notif.setSentAt(new Date());

        notificationRepository.save(notif);

        List<Notification> result = notificationRepository.findByUserId(1L);
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getContent()).isEqualTo("Test notification");
    }

    @Test
    @DisplayName("Should find unread notifications")
    void shouldFindUnread() {
        Notification notif = new Notification();
        notif.setUserId(1L);
        notif.setTaskId(2L);
        notif.setContent("Not read");
        notif.setRead(false);
        notificationRepository.save(notif);

        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(1L);
        assertThat(unread).isNotEmpty();
        assertThat(unread.get(0).isRead()).isFalse();
    }
}
