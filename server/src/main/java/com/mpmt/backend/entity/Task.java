package com.mpmt.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private LocalDate dueDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityType priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status;

    // ⬇️ Verrouille la colonne: non null et non updatable
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    // ---- Relation JPA propre vers Project ----
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore // on masque l'objet complet au JSON pour éviter les cycles & gros payloads
    private Project project;

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public PriorityType getPriority() { return priority; }
    public void setPriority(PriorityType priority) { this.priority = priority; }

    public StatusType getStatus() { return status; }
    public void setStatus(StatusType status) { this.status = status; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    // ====== Compat API : expose projectId ======
    @JsonProperty("projectId")
    public Long getProjectId() {
        return (project != null) ? project.getId() : null;
    }

    @JsonProperty("projectId")
    public void setProjectId(Long projectId) {
        if (projectId == null) {
            this.project = null;
        } else {
            Project p = new Project();
            p.setId(projectId);
            this.project = p;
        }
    }
}
