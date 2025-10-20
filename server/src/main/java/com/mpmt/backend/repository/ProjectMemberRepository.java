//package com.mpmt.backend.repository;
//
//import com.mpmt.backend.entity.ProjectMember;
//import com.mpmt.backend.entity.Project;
//import com.mpmt.backend.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//import java.util.List;
//
//public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
//    Optional<ProjectMember> findByUserAndProject(User user, Project project);
//    List<ProjectMember> findByProject(Project project);
//    List<ProjectMember> findByUser(User user);
//    List<ProjectMember> findByUserId(Long userId);
//    List<ProjectMember> findByProjectId(Long projectId);
//    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
//
//}


package com.mpmt.backend.repository;

import com.mpmt.backend.entity.ProjectMember;
import com.mpmt.backend.entity.Project;
import com.mpmt.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // Requêtes par association (entités)
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByUser(User user);
    Optional<ProjectMember> findByUserAndProject(User user, Project project);

    // Requêtes par clé étrangère (via @ManyToOne)
    List<ProjectMember> findByProject_Id(Long projectId);
    List<ProjectMember> findByUser_Id(Long userId);
    Optional<ProjectMember> findByUser_IdAndProject_Id(Long userId, Long projectId);
}