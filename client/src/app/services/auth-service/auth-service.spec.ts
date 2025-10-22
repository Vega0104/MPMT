import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService, SignupData, AuthResponse } from './auth-service.service';
import { API_BASE_URL } from '../../api-url';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/auth`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should signup user', () => {
    const signupData: SignupData = {
      username: 'testuser',
      email: 'test@test.com',
      password: 'password123'
    };
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      userId: 1
    };

    service.signup(signupData).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${baseUrl}/signup`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(signupData);
    req.flush(mockResponse);
  });

  it('should login and save token', () => {
    const mockResponse: AuthResponse = {
      token: 'fake-token',
      username: 'testuser',
      userId: 1
    };

    service.login('test@test.com', 'password123').subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(localStorage.getItem('auth_token')).toBe('fake-token');
      expect(localStorage.getItem('user_id')).toBe('1');
    });

    const req = httpMock.expectOne(`${baseUrl}/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should save and get token', () => {
    service.saveToken('test-token');
    expect(service.getToken()).toBe('test-token');
  });

  it('should logout and remove token', () => {
    localStorage.setItem('auth_token', 'test-token');
    service.logout();
    expect(localStorage.getItem('auth_token')).toBeNull();
  });

  it('should check if logged in', () => {
    expect(service.isLoggedIn()).toBe(false);

    localStorage.setItem('auth_token', 'test-token');
    expect(service.isLoggedIn()).toBe(true);
  });

  it('should get current user id', () => {
    localStorage.setItem('auth_token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c');
    localStorage.setItem('user_id', '5');

    expect(service.getCurrentUserId()).toBe(5);
  });

  it('should return null if no token', () => {
    expect(service.getCurrentUserId()).toBeNull();
  });
});
