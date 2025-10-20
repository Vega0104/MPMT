package com.mpmt.backend.service;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.ProjectRepository;
import com.mpmt.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import com.mpmt.backend.entity.RoleType;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectMemberService(
            ProjectMemberRepository projectMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectMember> getAllMembers() {
        return projectMemberRepository.findAll();
    }

    public Optional<ProjectMember> getById(Long id) {
        return projectMemberRepository.findById(id);
    }

    public ProjectMember createProjectMember(ProjectMember member) {
        // Charger le Project et User depuis la DB
        Project project = projectRepository.findById(member.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(member.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        member.setProject(project);
        member.setUser(user);

        return projectMemberRepository.save(member);
    }

    // NOUVELLE MÉTHODE pour compatibilité avec ProjectController
    public ProjectMember createMember(ProjectMember member) {
        // Si on utilise les IDs directement
        if (member.getProjectId() != null && member.getUserId() != null) {
            Project project = projectRepository.findById(member.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            User user = userRepository.findById(member.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            member.setProject(project);
            member.setUser(user);
        }

        return projectMemberRepository.save(member);
    }



    public List<ProjectMember> getByProject(Project project) {
        return projectMemberRepository.findByProject(project);
    }

    public List<ProjectMember> getByUser(User user) {
        return projectMemberRepository.findByUser(user);
    }

    public Optional<ProjectMember> getByUserAndProject(User user, Project project) {
        return projectMemberRepository.findByUserAndProject(user, project);
    }

    public void deleteProjectMember(Long id) {
        projectMemberRepository.deleteById(id);
    }

    public List<Project> findProjectsByUserId(Long userId) {
        List<ProjectMember> memberships = projectMemberRepository.findByUser_Id(userId); // <-- conserve findByUser_Id
        return memberships.stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    public List<ProjectMember> getMembersByProjectId(Long projectId) {
        return projectMemberRepository.findByProject_Id(projectId);
    }

    public List<ProjectMember> findByProjectId(Long projectId) {
        return projectMemberRepository.findByProject_Id(projectId);
    }

    public List<ProjectMember> findMembersByProjectId(Long projectId) {
        return projectMemberRepository.findByProject_Id(projectId);
    }

    public Optional<ProjectMember> updateRole(Long projectMemberId, String newRole) {
        Optional<ProjectMember> pmOpt = projectMemberRepository.findById(projectMemberId);
        if (pmOpt.isPresent()) {
            ProjectMember pm = pmOpt.get();
            pm.setRole(RoleType.valueOf(newRole));
            projectMemberRepository.save(pm);
            return Optional.of(pm);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProjectMember> getByUserIdAndProjectId(Long userId, Long projectId) {
        return projectMemberRepository.findByUser_IdAndProject_Id(userId, projectId);
    }
}