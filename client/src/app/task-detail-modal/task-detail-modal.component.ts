import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { firstValueFrom } from 'rxjs';

import { Task, TaskService } from '../services/task-service/task.service';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';

type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';
type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';
type Assignment = { id: number; taskId: number; projectMemberId: number };

@Component({
  selector: 'app-task-detail-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-detail-modal.component.html'
})
export class TaskDetailModalComponent implements OnInit {
  @Input() task!: Task;
  @Output() close = new EventEmitter<void>();
  @Output() statusUpdated = new EventEmitter<void>();

  form = {
    name: '',
    description: '',
    status: 'TODO' as TaskStatus,
    priority: 'MEDIUM' as TaskPriority,
    dueDate: '' as string | '',
    endDate: '' as string | ''
  };

  assignments: Assignment[] = [];
  allProjectMembers: ProjectMember[] = [];
  assignedMembers: ProjectMember[] = [];
  selectedMemberIds = new Set<number>();

  isSubmitting = false;
  error = '';

  constructor(
    private taskAssignmentService: TaskAssignmentService,
    private projectMemberService: ProjectMemberService,
    private taskService: TaskService
  ) {}

  ngOnInit() {
    this.hydrateFormFromTask();
    this.loadAssignments();
    this.loadProjectMembers();
  }

  private hydrateFormFromTask() {
    this.form.name = this.task?.name ?? '';
    this.form.description = this.task?.description ?? '';
    this.form.status = (this.task?.status as TaskStatus) ?? 'TODO';
    this.form.priority = (this.task?.priority as TaskPriority) ?? 'MEDIUM';
    this.form.dueDate = (this.task as any)?.dueDate ?? '';
    this.form.endDate = (this.task as any)?.endDate ?? '';
  }

  private loadAssignments() {
    this.taskAssignmentService.getByTaskId(this.task.id).subscribe({
      next: (assignments) => {
        this.assignments = assignments ?? [];
        this.selectedMemberIds = new Set<number>(this.assignments.map(a => a.projectMemberId));
        if (this.allProjectMembers.length) this.refreshAssignedMembersView();
      },
      error: () => { this.assignments = []; }
    });
  }

  private loadProjectMembers() {
    this.projectMemberService.getMembersByProjectId(this.task.projectId).subscribe({
      next: (members) => {
        this.allProjectMembers = (members ?? []).filter((m): m is ProjectMember => !!m && !!m.id);
        this.refreshAssignedMembersView();
      },
      error: () => { this.allProjectMembers = []; }
    });
  }

  private refreshAssignedMembersView() {
    const currentIds = new Set<number>(this.assignments.map(a => a.projectMemberId));
    this.assignedMembers = this.allProjectMembers.filter(m => currentIds.has(m.id));
  }

  toggleMember(memberId: number, checked: boolean) {
    if (checked) this.selectedMemberIds.add(memberId);
    else this.selectedMemberIds.delete(memberId);
  }

  /** Getter pour éviter l'expression complexe dans le template */
  get assignedUsernames(): string {
    return this.assignedMembers
      .map(m => m.user?.username || '—')
      .join(', ');
  }

  private toLocalDateOrEmpty(input: string | ''): string {
    return input ? input : '';
  }

  async onSave() {
    if (!this.form.name.trim()) {
      this.error = 'Title is required';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    const payload: Partial<Task> = {
      name: this.form.name.trim(),
      description: this.form.description?.trim() || '',
      status: this.form.status,
      priority: this.form.priority,
      dueDate: this.toLocalDateOrEmpty(this.form.dueDate),
      endDate: this.toLocalDateOrEmpty(this.form.endDate)
    };

    try {
      await firstValueFrom(this.taskService.updateTask(this.task.id, payload));

      const currentIds = new Set<number>(this.assignments.map(a => a.projectMemberId));
      const targetIds = this.selectedMemberIds;

      const toAdd = [...targetIds].filter(id => !currentIds.has(id));
      const toRemove = this.assignments.filter(a => !targetIds.has(a.projectMemberId));

      await Promise.all([
        ...toAdd.map(pmId => firstValueFrom(this.taskAssignmentService.assignTask(this.task.id, pmId))),
        ...toRemove.map(a => firstValueFrom(this.taskAssignmentService.removeAssignment(a.id)))
      ]);

      this.task.name = payload.name!;
      this.task.description = payload.description!;
      this.task.status = payload.status as TaskStatus;
      this.task.priority = payload.priority as TaskPriority;
      this.task.dueDate = payload.dueDate ?? '';
      (this.task as any).endDate = payload.endDate ?? '';

      this.isSubmitting = false;
      this.statusUpdated.emit();
      this.onClose();
    } catch (err: any) {
      this.error = err?.error?.message || 'Failed to save changes';
      this.isSubmitting = false;
    }
  }

  onClose() {
    this.close.emit();
  }
}
