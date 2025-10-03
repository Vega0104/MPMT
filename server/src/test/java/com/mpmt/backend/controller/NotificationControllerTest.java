package com.mpmt.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpmt.backend.entity.Notification;
import com.mpmt.backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notif;

    @BeforeEach
    void setup() {
        notif = new Notification();
        notif.setId(1L);
        notif.setUserId(1L);
        notif.setTaskId(2L);
        notif.setContent("Test notification");
        notif.setRead(false);
        notif.setSentAt(new Date());
    }

    @Test
    void shouldReturnAllNotifications() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(Arrays.asList(notif));
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test notification"));
    }

    @Test
    void shouldCreateNotification() throws Exception {
        when(notificationService.createNotification(any(Notification.class))).thenReturn(notif);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notif)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test notification"));
    }

    @Test
    void shouldMarkAsRead() throws Exception {
        Mockito.doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isNoContent());
    }
}
