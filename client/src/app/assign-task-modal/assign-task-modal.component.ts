import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service'; // <- fix 'task'
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';

@Component({
  selector: 'app-assign-task-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-task-modal.component.html'
})
export class AssignTaskModalComponent implements OnInit, OnChanges {
  @Input() taskId!: number;
  @Input() projectId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() assigned = new EventEmitter<void>();

  members: ProjectMember[] = [];
  assignments: { id: number; taskId: number; projectMemberId: number }[] = [];
  selectedMemberId: number | null = null;
  isSubmitting = false;
  error = '';

  constructor(
    private taskAssignmentService: TaskAssignmentService,
    private projectMemberService: ProjectMemberService
  ) {}

  ngOnInit() {
    // Si les inputs sont déjà dispo au premier cycle, on peut charger.
    this.tryLoad();
  }

  ngOnChanges(changes: SimpleChanges) {
    // Recharge dès que taskId/projectId deviennent disponibles ou changent
    if (changes['projectId'] || changes['taskId']) {
      this.tryLoad();
    }
  }

  private tryLoad() {
    if (!this.projectId || !this.taskId) return;
    this.loadMembers();
    this.loadAssignments();
  }

  private loadMembers() {
    this.error = '';
    this.projectMemberService.getMembersByProjectId(this.projectId).subscribe({
      next: (data) => {
        this.members = data;
        if (!this.members.length) {
          this.error = 'No members found for this project.';
        }
      },
      error: (err) => {
        console.error('Error loading members', err);
        this.error = err?.error?.message || 'Failed to load project members';
      }
    });
  }

  private loadAssignments() {
    this.taskAssignmentService.getByTaskId(this.taskId).subscribe({
      next: (data) => this.assignments = data,
      error: (err) => {
        console.error('Error loading assignments', err);
        // non bloquant : on peut garder la liste de membres visible
      }
    });
  }

  isAssigned(memberId: number): boolean {
    return this.assignments.some(a => a.projectMemberId === memberId);
  }

  onAssign() {
    if (this.selectedMemberId == null) {
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
        console.error('Error assigning task', err);
        this.error = err?.error?.message || 'Failed to assign task';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
