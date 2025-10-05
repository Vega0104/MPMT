import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../services/task-service/task.service';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';
import { TaskService } from '../services/task-service/task.service';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-task-detail-modal',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './task-detail-modal.component.html'
})
export class TaskDetailModalComponent implements OnInit {
  @Input() task!: Task;
  @Output() close = new EventEmitter<void>();
  @Output() statusUpdated = new EventEmitter<void>();

  isUpdatingStatus = false;
  assignments: any[] = [];
  assignedMembers: ProjectMember[] = [];

  constructor(
    private taskAssignmentService: TaskAssignmentService,
    private projectMemberService: ProjectMemberService,
    private taskService: TaskService
  ) {}

  ngOnInit() {
    this.loadAssignments();
  }

  loadAssignments() {
    this.taskAssignmentService.getByTaskId(this.task.id).subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loadAssignedMembers();
      }
    });
  }

  loadAssignedMembers() {
    this.projectMemberService.getAllMembers().subscribe({
      next: (members) => {
        this.assignedMembers = members.filter(m =>
          this.assignments.some(a => a.projectMemberId === m.id)
        );
      }
    });
  }

  onClose() {
    this.close.emit();
  }

  onStatusChange(newStatus: string) {
    this.isUpdatingStatus = true;

    this.taskService.updateTaskStatus(this.task.id, newStatus).subscribe({
      next: () => {
        this.task.status = newStatus as 'TODO' | 'IN_PROGRESS' | 'DONE';
        this.isUpdatingStatus = false;
        this.statusUpdated.emit();
      },
      error: (err) => {
        console.error('Error updating status', err);
        this.isUpdatingStatus = false;
      }
    });
  }
}
