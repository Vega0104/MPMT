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
    // 1) Récupère uniquement les membres DU PROJET de la tâche
    this.projectMemberService.getMembersByProjectId(this.task.projectId).subscribe({
      next: (members) => {
        // 2) Garde-fou: enlève les éléments falsy
        const safeMembers = (members ?? []).filter((m): m is ProjectMember => !!m && !!m.id);

        // 3) Crée un Set des IDs de ProjectMember assignés à la tâche
        const assignedIds = new Set((this.assignments ?? [])
          .filter(a => a && a.projectMemberId != null)
          .map(a => a.projectMemberId as number));

        // 4) Filtre final
        this.assignedMembers = safeMembers.filter(m => assignedIds.has(m.id));
      },
      error: (err) => {
        console.error('Error loading project members', err);
        this.assignedMembers = [];
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
