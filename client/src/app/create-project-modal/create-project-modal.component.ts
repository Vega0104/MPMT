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
  isSubmitting = false;
  error = '';

  constructor(private projectService: ProjectService) {}

  onSubmit() {
    if (!this.projectName.trim()) {
      this.error = 'Project name is required';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.projectService.createProject({
      name: this.projectName,
      description: this.projectDescription,
      startDate: new Date().toISOString().split('T')[0]
    }).subscribe({
      next: () => {
        this.projectCreated.emit();
        this.onClose();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create project';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
