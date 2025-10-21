// src/main/java/com/mpmt/backend/dto/UpdateTaskRequest.java
package com.mpmt.backend.dto;

import com.mpmt.backend.entity.PriorityType;
import com.mpmt.backend.entity.StatusType;

public class UpdateTaskRequest {
    private String name;
    private String description;
    private StatusType status;
    private PriorityType priority;
    // Laisse le controller/service convertir "" -> null
    private String dueDate; // "YYYY-MM-DD" ou "" (pas null côté front)
    private String endDate; // "YYYY-MM-DD" ou ""

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public StatusType getStatus() { return status; }
    public void setStatus(StatusType status) { this.status = status; }
    public PriorityType getPriority() { return priority; }
    public void setPriority(PriorityType priority) { this.priority = priority; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
