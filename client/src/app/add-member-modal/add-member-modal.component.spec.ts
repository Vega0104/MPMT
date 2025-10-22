import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddMemberModalComponent } from './add-member-modal.component';
import { ProjectMemberService } from '../services/project-member-service/project-member.service';
import { UserService, User } from '../services/user-service/user.service';
import { of, throwError } from 'rxjs';

describe('AddMemberModalComponent', () => {
  let component: AddMemberModalComponent;
  let fixture: ComponentFixture<AddMemberModalComponent>;
  let mockUserService: jest.Mocked<Partial<UserService>>;
  let mockProjectMemberService: jest.Mocked<Partial<ProjectMemberService>>;

  const mockUsers: User[] = [
    { id: 1, username: 'user1', email: 'user1@test.com', },
    { id: 2, username: 'user2', email: 'user2@test.com', },
  ];

  beforeEach(async () => {
    mockUserService = {
      getAllUsers: jest.fn().mockReturnValue(of(mockUsers))
    };

    mockProjectMemberService = {
      addMember: jest.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [AddMemberModalComponent]
    })
      .overrideComponent(AddMemberModalComponent, {
        set: {
          providers: [
            { provide: UserService, useValue: mockUserService },
            { provide: ProjectMemberService, useValue: mockProjectMemberService },
          ]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(AddMemberModalComponent);
    component = fixture.componentInstance;
    component.projectId = 1;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load users on init', () => {
    fixture.detectChanges();

    expect(mockUserService.getAllUsers).toHaveBeenCalled();
    expect(component.users).toEqual(mockUsers);
    expect(component.users.length).toBe(2);
  });

  it('should handle error when loading users', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    mockUserService.getAllUsers = jest.fn().mockReturnValue(
      throwError(() => new Error('Load error'))
    );

    fixture.detectChanges();

    // @ts-ignore
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error loading users', expect.any(Error));
    consoleErrorSpy.mockRestore();
  });

  it('should emit close event when onClose is called', () => {
    const emitSpy = jest.spyOn(component.close, 'emit');

    component.onClose();

    expect(emitSpy).toHaveBeenCalled();
  });

  it('should show error if no user is selected', () => {
    component.selectedUserId = null;

    component.onSubmit();

    expect(component.error).toBe('Please select a user');
    expect(mockProjectMemberService.addMember).not.toHaveBeenCalled();
  });

  it('should add member and emit events on successful submission', () => {
    fixture.detectChanges();
    const memberAddedSpy = jest.spyOn(component.memberAdded, 'emit');
    const closeSpy = jest.spyOn(component.close, 'emit');

    component.selectedUserId = 1;
    component.selectedRole = 'MEMBER';

    component.onSubmit();

    expect(component.isSubmitting).toBe(true);
    expect(mockProjectMemberService.addMember).toHaveBeenCalledWith({
      projectId: 1,
      userId: 1,
      role: 'MEMBER'
    });
    expect(memberAddedSpy).toHaveBeenCalled();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('should add member with ADMIN role', () => {
    fixture.detectChanges();

    component.selectedUserId = 2;
    component.selectedRole = 'ADMIN';

    component.onSubmit();

    expect(mockProjectMemberService.addMember).toHaveBeenCalledWith({
      projectId: 1,
      userId: 2,
      role: 'ADMIN'
    });
  });

  it('should add member with OBSERVER role', () => {
    fixture.detectChanges();

    component.selectedUserId = 2;
    component.selectedRole = 'OBSERVER';

    component.onSubmit();

    expect(mockProjectMemberService.addMember).toHaveBeenCalledWith({
      projectId: 1,
      userId: 2,
      role: 'OBSERVER'
    });
  });

  it('should handle error on submission', () => {
    fixture.detectChanges();
    const errorResponse = { error: { message: 'User already member' } };
    mockProjectMemberService.addMember = jest.fn().mockReturnValue(
      throwError(() => errorResponse)
    );

    component.selectedUserId = 1;
    component.selectedRole = 'MEMBER';

    component.onSubmit();

    expect(component.error).toBe('User already member');
    expect(component.isSubmitting).toBe(false);
  });

  it('should handle error without message on submission', () => {
    fixture.detectChanges();
    mockProjectMemberService.addMember = jest.fn().mockReturnValue(
      throwError(() => new Error('Network error'))
    );

    component.selectedUserId = 1;
    component.selectedRole = 'MEMBER';

    component.onSubmit();

    expect(component.error).toBe('Failed to add member');
    expect(component.isSubmitting).toBe(false);
  });

  it('should clear error on successful submission', () => {
    fixture.detectChanges();
    component.error = 'Previous error';
    component.selectedUserId = 1;
    component.selectedRole = 'MEMBER';

    component.onSubmit();

    expect(component.error).toBe('');
  });

  it('should set isSubmitting to true when submitting', () => {
    fixture.detectChanges();
    component.selectedUserId = 1;

    component.onSubmit();

    expect(component.isSubmitting).toBe(true);
  });

  it('should have default role as MEMBER', () => {
    expect(component.selectedRole).toBe('MEMBER');
  });
});
