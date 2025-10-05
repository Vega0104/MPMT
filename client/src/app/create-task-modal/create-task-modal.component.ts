import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../services/task-service/task.service';
import { AuthService } from '../services/auth-service/auth-service.service';

@Component({
  selector: 'app-create-task-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-task-modal.component.html'
})
export class CreateTaskModalComponent {
  @Input() projectId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() taskCreated = new EventEmitter<void>();

  taskName = '';
  taskDescription = '';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' = 'MEDIUM';
  dueDate = '';
  isSubmitting = false;
  error = '';

  constructor(
    private taskService: TaskService,
    private authService: AuthService
  ) {}

  onSubmit() {
    if (!this.taskName.trim()) {
      this.error = 'Task name is required';
      return;
    }

    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.error = 'User not authenticated';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.taskService.createTask({
      name: this.taskName,
      description: this.taskDescription,
      priority: this.priority,
      status: 'TODO',
      dueDate: this.dueDate,
      projectId: this.projectId,
      createdBy: userId
    }).subscribe({
      next: () => {
        this.taskCreated.emit();
        this.onClose();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to create task';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
