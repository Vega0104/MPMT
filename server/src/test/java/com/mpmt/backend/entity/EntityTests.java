package com.mpmt.backend.entity;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class StatusTypeTest {
    @Test
    void testEnumValues() {
        assertEquals(3, StatusType.values().length);
        assertEquals(StatusType.TODO, StatusType.valueOf("TODO"));
        assertEquals(StatusType.IN_PROGRESS, StatusType.valueOf("IN_PROGRESS"));
        assertEquals(StatusType.DONE, StatusType.valueOf("DONE"));
    }
}

class PriorityTypeTest {
    @Test
    void testEnumValues() {
        assertEquals(3, PriorityType.values().length);
        assertEquals(PriorityType.LOW, PriorityType.valueOf("LOW"));
        assertEquals(PriorityType.MEDIUM, PriorityType.valueOf("MEDIUM"));
        assertEquals(PriorityType.HIGH, PriorityType.valueOf("HIGH"));
    }
}

class RoleTypeTest {
    @Test
    void testEnumValues() {
        assertEquals(3, RoleType.values().length);
        assertEquals(RoleType.ADMIN, RoleType.valueOf("ADMIN"));
        assertEquals(RoleType.MEMBER, RoleType.valueOf("MEMBER"));
        assertEquals(RoleType.OBSERVER, RoleType.valueOf("OBSERVER"));
    }
}

class NotificationTest {
    @Test
    void testGettersAndSetters() {
        Notification notification = new Notification();

        notification.setId(1L);
        assertEquals(1L, notification.getId());

        notification.setUserId(10L);
        assertEquals(10L, notification.getUserId());

        notification.setTaskId(20L);
        assertEquals(20L, notification.getTaskId());

        notification.setContent("Test notification");
        assertEquals("Test notification", notification.getContent());

        notification.setRead(true);
        assertTrue(notification.isRead());

        Date date = new Date();
        notification.setSentAt(date);
        assertEquals(date, notification.getSentAt());
    }

    @Test
    void testDefaultValues() {
        Notification notification = new Notification();
        assertFalse(notification.isRead());
        assertNotNull(notification.getSentAt());
    }
}

class TaskHistoryTest {
    @Test
    void testGettersAndSetters() {
        TaskHistory history = new TaskHistory();

        history.setId(1L);
        assertEquals(1L, history.getId());

        history.setTaskId(100L);
        assertEquals(100L, history.getTaskId());

        history.setChangedBy(50L);
        assertEquals(50L, history.getChangedBy());

        Date date = new Date();
        history.setChangeDate(date);
        assertEquals(date, history.getChangeDate());

        history.setChangeDescription("Status changed to DONE");
        assertEquals("Status changed to DONE", history.getChangeDescription());
    }

    @Test
    void testDefaultChangeDate() {
        TaskHistory history = new TaskHistory();
        assertNotNull(history.getChangeDate());
    }
}
