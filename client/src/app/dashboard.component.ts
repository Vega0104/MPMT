import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService, Project, ProjectStats } from './services/project-service/project.service';
import { TaskService, Task } from './services/task-service/task.service';
import { AuthService } from './services/auth-service/auth-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: true,
  imports: [CommonModule]
})
export class DashboardComponent implements OnInit {
  userName = 'User';
  projects: Array<Project & { stats?: ProjectStats }> = [];
  tasks: Task[] = [];
  loading = true;

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadProjects();
    this.loadTasks();
  }

  loadProjects() {
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        // Charger les stats pour chaque projet
        projects.forEach(project => {
          this.projectService.getProjectStats(project.id).subscribe({
            next: (stats) => {
              const p = this.projects.find(pr => pr.id === project.id);
              if (p) p.stats = stats;
            }
          });
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading projects', err);
        this.loading = false;
      }
    });
  }

  loadTasks() {
    this.taskService.getAllTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks.slice(0, 5); // 5 premières tâches
      },
      error: (err) => console.error('Error loading tasks', err)
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
