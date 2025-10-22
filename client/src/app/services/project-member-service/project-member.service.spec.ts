import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectMemberService, ProjectMember, AddMemberRequest } from './project-member.service';
import { API_BASE_URL } from '../../api-url';

describe('ProjectMemberService', () => {
  let service: ProjectMemberService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/project-members`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectMemberService]
    });
    service = TestBed.inject(ProjectMemberService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all members', () => {
    const mockMembers: ProjectMember[] = [
      { id: 1, role: 'ADMIN', user: { id: 1, username: 'user1' } }
    ];

    service.getAllMembers().subscribe(members => {
      expect(members).toEqual(mockMembers);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockMembers);
  });

  it('should get members by project id', () => {
    const mockResponse = [
      { id: 1, role: 'ADMIN', user: { id: 1, username: 'user1', email: 'user1@test.com' }, project: { id: 1, name: 'Project' } }
    ];

    service.getMembersByProjectId(1).subscribe(members => {
      expect(members.length).toBe(1);
      expect(members[0].role).toBe('ADMIN');
      expect(members[0].user.username).toBe('user1');
    });

    const req = httpMock.expectOne(`${API_BASE_URL}/projects/1/members`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should filter out invalid members', () => {
    const mockResponse = [
      { id: 1, role: 'ADMIN', user: { id: 1, username: 'user1' } },
      { id: null, role: 'MEMBER', user: null },
      { id: 2, role: 'MEMBER', user: { id: 2, username: 'user2' } }
    ];

    service.getMembersByProjectId(1).subscribe(members => {
      expect(members.length).toBe(2);
    });

    const req = httpMock.expectOne(`${API_BASE_URL}/projects/1/members`);
    req.flush(mockResponse);
  });

  it('should add member with uppercase role', () => {
    const request: AddMemberRequest = {
      projectId: 1,
      userId: 2,
      role: 'member'
    };
    const mockResponse: ProjectMember = {
      id: 1,
      role: 'MEMBER',
      user: { id: 2, username: 'user2' }
    };

    service.addMember(request).subscribe(member => {
      expect(member).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.role).toBe('MEMBER');
    req.flush(mockResponse);
  });

  it('should remove member', () => {
    service.removeMember(1).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should update member role', () => {
    const mockResponse: ProjectMember = {
      id: 1,
      role: 'ADMIN',
      user: { id: 1, username: 'user1' }
    };

    service.updateRole(1, 'admin').subscribe(member => {
      expect(member.role).toBe('ADMIN');
    });

    const req = httpMock.expectOne(`${baseUrl}/1/role`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.role).toBe('ADMIN');
    req.flush(mockResponse);
  });
});
