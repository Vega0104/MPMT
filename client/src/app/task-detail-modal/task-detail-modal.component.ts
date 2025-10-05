import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../services/task-service/task.service';
import { TaskAssignmentService } from '../services/tast-assignment-service/task-assignment.service';
import { ProjectMemberService, ProjectMember } from '../services/project-member-service/project-member.service';

@Component({
  selector: 'app-task-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './task-detail-modal.component.html'
})
export class TaskDetailModalComponent implements OnInit {
  @Input() task!: Task;
  @Output() close = new EventEmitter<void>();

  assignments: any[] = [];
  assignedMembers: ProjectMember[] = [];

  constructor(
    private taskAssignmentService: TaskAssignmentService,
    private projectMemberService: ProjectMemberService
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
}
