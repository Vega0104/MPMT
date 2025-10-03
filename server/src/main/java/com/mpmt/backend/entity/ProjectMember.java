package com.mpmt.backend.entity;
import com.mpmt.backend.entity.RoleType;


import jakarta.persistence.*;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    // Getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}
