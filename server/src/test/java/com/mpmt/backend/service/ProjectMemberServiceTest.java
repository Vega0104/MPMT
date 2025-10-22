package com.mpmt.backend.service;

import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.RoleType;
import com.mpmt.backend.entity.User;
import com.mpmt.backend.repository.ProjectMemberRepository;
import com.mpmt.backend.repository.ProjectRepository;
import com.mpmt.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectMemberServiceTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectMemberService service;

    private User user;
    private Project project;
    private ProjectMember member;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        project = new Project();
        project.setId(10L);
        project.setName("TestProject");

        member = new ProjectMember();
        member.setId(100L);
        member.setUser(user);
        member.setProject(project);
        member.setRole(RoleType.MEMBER);
    }

    @Test
    void shouldGetAllMembers() {
        when(projectMemberRepository.findAll()).thenReturn(Arrays.asList(member));
        List<ProjectMember> result = service.getAllMembers();
        assertThat(result).hasSize(1);
        verify(projectMemberRepository).findAll();
    }

    @Test
    void shouldGetById() {
        when(projectMemberRepository.findById(100L)).thenReturn(Optional.of(member));
        Optional<ProjectMember> result = service.getById(100L);
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(RoleType.MEMBER);
    }

    @Test
    void shouldCreateProjectMember() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(member);

        ProjectMember created = service.createProjectMember(member);

        assertThat(created).isNotNull();
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void shouldFindProjectsByUserId() {
        when(projectMemberRepository.findByUser_Id(1L)).thenReturn(Arrays.asList(member));
        List<Project> projects = service.findProjectsByUserId(1L);
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("TestProject");
    }

    @Test
    void shouldUpdateRole() {
        when(projectMemberRepository.findById(100L)).thenReturn(Optional.of(member));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(member);

        Optional<ProjectMember> updated = service.updateRole(100L, "ADMIN");

        assertThat(updated).isPresent();
        assertThat(updated.get().getRole()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    void shouldDeleteProjectMember() {
        doNothing().when(projectMemberRepository).deleteById(100L);
        service.deleteProjectMember(100L);
        verify(projectMemberRepository).deleteById(100L);
    }
}