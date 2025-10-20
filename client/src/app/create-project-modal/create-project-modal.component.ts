import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../services/project-service/project.service';

@Component({
  selector: 'app-create-project-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-project-modal.component.html'
})
export class CreateProjectModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() projectCreated = new EventEmitter<void>();

  projectName = '';
  projectDescription = '';
  projectStartDate = '';
  isSubmitting = false;
  error = '';

  constructor(private projectService: ProjectService) {
    // Initialiser avec la date d'aujourd'hui par défaut
    const today = new Date();
    this.projectStartDate = today.toISOString().split('T')[0];
  }

  onSubmit() {
    // Validation
    if (!this.projectName.trim()) {
      this.error = 'Project name is required';
      return;
    }

    if (!this.projectStartDate) {
      this.error = 'Start date is required';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    // Créer le projet
    this.projectService.createProject({
      name: this.projectName.trim(),
      description: this.projectDescription.trim(),
      startDate: this.projectStartDate
    }).subscribe({
      next: (project) => {
        console.log('Project created successfully:', project);
        this.projectCreated.emit();
        this.onClose();
      },
      error: (err) => {
        console.error('Error creating project:', err);
        this.error = err.error?.message || 'Failed to create project';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
