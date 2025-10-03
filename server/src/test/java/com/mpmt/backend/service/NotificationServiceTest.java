package com.mpmt.backend.service;

import com.mpmt.backend.entity.Notification;
import com.mpmt.backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    public NotificationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should get all notifications")
    void shouldGetAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(new Notification()));
        assertThat(notificationService.getAllNotifications()).hasSize(1);
    }

    @Test
    @DisplayName("Should create notification")
    void shouldCreateNotification() {
        Notification notif = new Notification();
        notif.setContent("Test");
        when(notificationRepository.save(notif)).thenReturn(notif);

        Notification saved = notificationService.createNotification(notif);
        assertThat(saved.getContent()).isEqualTo("Test");
        verify(notificationRepository, times(1)).save(notif);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void shouldMarkAsRead() {
        Notification notif = new Notification();
        notif.setId(1L);
        notif.setRead(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        when(notificationRepository.save(notif)).thenReturn(notif);

        notificationService.markAsRead(1L);
        assertThat(notif.isRead()).isTrue();
        verify(notificationRepository).save(notif);
    }
}
