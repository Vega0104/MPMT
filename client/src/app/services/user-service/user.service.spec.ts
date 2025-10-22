import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService, User } from './user.service';
import { API_BASE_URL } from '../../api-url';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/users`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all users', () => {
    const mockUsers: User[] = [
      { id: 1, username: 'user1', email: 'user1@test.com' },
      { id: 2, username: 'user2', email: 'user2@test.com' }
    ];

    service.getAllUsers().subscribe(users => {
      expect(users).toEqual(mockUsers);
      expect(users.length).toBe(2);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });
});
