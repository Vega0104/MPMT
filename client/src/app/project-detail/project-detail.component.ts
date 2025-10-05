import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService, Project } from '../services/project-service/project.service';
import { TaskService, Task } from '../services/task-service/task.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';
import { CreateTaskModalComponent } from '../create-task-modal/create-task-modal.component';
import { AddMemberModalComponent } from '../add-member-modal/add-member-modal.component';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, CreateTaskModalComponent, AddMemberModalComponent],
  templateUrl: './project-detail.component.html'
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  tasks: Task[] = [];
  members: ProjectMember[] = [];
  projectId: number = 0;
  showCreateTaskModal = false;
  showAddMemberModal = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private projectMemberService: ProjectMemberService
  ) {}

  ngOnInit() {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProject();
    this.loadTasks();
    this.loadMembers();
  }

  loadProject() {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (data) => this.project = data,
      error: (err) => console.error('Error loading project', err)
    });
  }

  loadTasks() {
    this.taskService.getAllTasks().subscribe({
      next: (data) => this.tasks = data.filter(t => t.projectId === this.projectId),
      error: (err) => console.error('Error loading tasks', err)
    });
  }

  loadMembers() {
    this.projectMemberService.getAllMembers().subscribe({
      next: (data) => {
        console.log('All members from API:', data);
        console.log('Current projectId:', this.projectId);
        this.members = data.filter(m => m.project.id === this.projectId);
        console.log('Filtered members:', this.members);
      },
      error: (err) => console.error('Error loading members', err)
    });
  }

  openCreateTaskModal() {
    this.showCreateTaskModal = true;
  }

  closeCreateTaskModal() {
    this.showCreateTaskModal = false;
  }

  onTaskCreated() {
    this.loadTasks();
  }

  openAddMemberModal() {
    this.showAddMemberModal = true;
  }

  closeAddMemberModal() {
    this.showAddMemberModal = false;
  }

  onMemberAdded() {
    this.loadMembers();
  }

  deleteProject() {
    if (!confirm('Delete this project?')) return;

    this.projectService.deleteProject(this.projectId).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => console.error('Error deleting project', err)
    });
  }
}
