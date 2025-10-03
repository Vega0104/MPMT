package com.mpmt.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_histories")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId; // ID de la tâche concernée

    @Column(nullable = false)
    private Long changedBy; // ID de l'utilisateur ayant fait le changement

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date changeDate = new Date();

    @Column(nullable = false)
    private String changeDescription;

    // Getters et setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getChangedBy() { return changedBy; }
    public void setChangedBy(Long changedBy) { this.changedBy = changedBy; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
}
