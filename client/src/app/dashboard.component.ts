import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectService, Project } from './services/project-service/project.service';
import { TaskService, Task } from './services/task-service/task.service';
import { CreateProjectModalComponent } from './create-project-modal/create-project-modal.component';
import { AuthService } from './services/auth-service/auth-service.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, CreateProjectModalComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  projects: Project[] = [];
  tasks: Task[] = [];
  loading = false;
  showCreateProjectModal = false;

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadProjects();
    this.loadTasks();
  }

  loadProjects() {
    this.loading = true;
    this.projectService.getAllProjects()
      .subscribe({
        next: (data) => {
          this.projects = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading projects', err);
          this.loading = false;
        }
      });
  }

  loadTasks() {
    this.taskService.getAllTasks()
      .subscribe({
        next: (data) => this.tasks = data,
        error: (err) => console.error('Error loading tasks', err)
      });
  }

  openCreateProjectModal() {
    this.showCreateProjectModal = true;
  }

  closeCreateProjectModal() {
    this.showCreateProjectModal = false;
  }

  onProjectCreated() {
    this.loadProjects();
  }

  navigateToProject(id: number) {
    this.router.navigate(['/projects', id]);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
