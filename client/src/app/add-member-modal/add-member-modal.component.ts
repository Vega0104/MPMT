import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectMemberService } from '../services/project-member-service/project-member.service';
import { UserService, User } from '../services/user-service/user.service';

@Component({
  selector: 'app-add-member-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-member-modal.component.html'
})
export class AddMemberModalComponent implements OnInit {
  @Input() projectId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() memberAdded = new EventEmitter<void>();

  users: User[] = [];
  selectedUserId: number | null = null;
  selectedRole: 'ADMIN' | 'MEMBER' | 'OBSERVER' = 'MEMBER';
  isSubmitting = false;
  error = '';

  constructor(
    private projectMemberService: ProjectMemberService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Error loading users', err)
    });
  }

  onSubmit() {
    if (!this.selectedUserId) {
      this.error = 'Please select a user';
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.projectMemberService.addMember({
      projectId: this.projectId,
      userId: this.selectedUserId,
      role: this.selectedRole
    }).subscribe({
      next: () => {
        this.memberAdded.emit();
        this.onClose();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to add member';
        this.isSubmitting = false;
      }
    });
  }

  onClose() {
    this.close.emit();
  }
}
