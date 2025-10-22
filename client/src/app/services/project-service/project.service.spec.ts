import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService, Project, ProjectStats } from './project.service';
import { API_BASE_URL } from '../../api-url';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/projects`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all projects', () => {
    const mockProjects: Project[] = [
      { id: 1, name: 'Project 1', description: 'Desc 1', startDate: '2025-01-01', createdAt: '2025-01-01' }
    ];

    service.getAllProjects().subscribe(projects => {
      expect(projects).toEqual(mockProjects);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockProjects);
  });

  it('should get project by id', () => {
    const mockProject: Project = {
      id: 1,
      name: 'Project 1',
      description: 'Description',
      startDate: '2025-01-01',
      createdAt: '2025-01-01'
    };

    service.getProjectById(1).subscribe(project => {
      expect(project).toEqual(mockProject);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProject);
  });

  it('should get project stats', () => {
    const mockStats: ProjectStats = {
      totalTasks: 10,
      todoCount: 3,
      inProgressCount: 4,
      doneCount: 3,
      progress: 70
    };

    service.getProjectStats(1).subscribe(stats => {
      expect(stats).toEqual(mockStats);
    });

    const req = httpMock.expectOne(`${baseUrl}/1/stats`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStats);
  });

  it('should create project', () => {
    const newProject: Partial<Project> = {
      name: 'New Project',
      description: 'Description',
      startDate: '2025-01-01'
    };
    const mockResponse: Project = {
      id: 1,
      ...newProject,
      createdAt: '2025-01-01'
    } as Project;

    service.createProject(newProject).subscribe(project => {
      expect(project).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newProject);
    req.flush(mockResponse);
  });

  it('should update project', () => {
    const updates: Partial<Project> = { name: 'Updated Name' };
    const mockResponse: Project = {
      id: 1,
      name: 'Updated Name',
      description: 'Desc',
      startDate: '2025-01-01',
      createdAt: '2025-01-01'
    };

    service.updateProject(1, updates).subscribe(project => {
      expect(project.name).toBe('Updated Name');
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updates);
    req.flush(mockResponse);
  });

  it('should delete project', () => {
    service.deleteProject(1).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
