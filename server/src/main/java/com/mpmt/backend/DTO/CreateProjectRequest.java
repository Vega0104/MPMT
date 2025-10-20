package com.mpmt.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class CreateProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private Date startDate;

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}