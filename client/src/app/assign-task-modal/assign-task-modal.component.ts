import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';

@Component({
  selector: 'app-assign-task-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-task-modal.component.html'
})
export class AssignTaskModalComponent implements OnInit {
  @Input() taskId!: number;
  @Input() projectId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() assigned = new EventEmitter<void>();

  members: ProjectMember[] = [];
  assignments: any[] = [];
  selectedMemberId: number | null = null;
  isSubmitting = false;
  error = '';

  constructor(
    private taskAssignmentService: TaskAssignmentService,
    private projectMemberService: ProjectMemberService
  ) {}

  ngOnInit() {
    this.loadMembers();
    this.loadAssignments();
  }

  loadMembers() {
    this.projectMemberService.getAllMembers().subscribe({
      next: (data) => {
        this.members = data.filter(m => m.project?.id === this.projectId);
      },
      error: (err) => console.error('Error loading members', err)
    });
  }

  loadAssignments() {
    this.taskAssignmentService.getByTaskId(this.taskId).subscribe({
      next: (data) => this.assignments = data,
      error: (err) => console.error('Error loading assignments', err)
    });
  }

  isAssigned(memberId: number): boolean {
    return this.assignments.some(a => a.projectMemberId === memberId);
  }

  onAssign() {
    if (!this.selectedMemberId) {
      this.error = 'Please select a member';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.taskAssignmentService.assignTask(this.taskId, this.selectedMemberId).subscribe({
      next: () => {
        this.loadAssignments();
        this.selectedMemberId = null;
        this.isSubmitting = false;
        this.assigned.emit();
      },
      error: (err) => {
        this.error = err.error || 'Failed to assign task';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
