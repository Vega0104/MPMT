package com.mpmt.backend.entity;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_member")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;

    // Getters & setters existants
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Date getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Date joinedAt) { this.joinedAt = joinedAt; }

    // Méthodes helper pour faciliter l'utilisation avec des IDs
    public void setUserId(Long userId) {
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            this.user = u;
        }
    }

    public void setProjectId(Long projectId) {
        if (projectId != null) {
            Project p = new Project();
            p.setId(projectId);
            this.project = p;
        }
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getProjectId() {
        return project != null ? project.getId() : null;
    }
}