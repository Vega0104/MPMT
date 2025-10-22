import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([])]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should have the temp-front title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('temp-front');
  });

  it('should render router outlet', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('router-outlet')).toBeTruthy();
  });

  it('should hide home button by default', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.showHomeButton).toBe(false);
  });

  it('should show home button when authenticated and not on dashboard', () => {
    localStorage.setItem('auth_token', 'fake-token');
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    app['updateHomeButtonVisibility']('/projects/1');

    expect(app.showHomeButton).toBe(true);
    localStorage.clear();
  });
});
