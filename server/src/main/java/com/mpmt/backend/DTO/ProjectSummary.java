package com.mpmt.backend.DTO;

import java.util.Date;

public class ProjectSummary {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date createdAt;

    public ProjectSummary(Long id, String name, String description, Date startDate, Date createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Date getStartDate() { return startDate; }
    public Date getCreatedAt() { return createdAt; }
}
