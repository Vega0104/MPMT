// src/app/app.routes.ts
import { Routes } from '@angular/router';

// Pages (standalone components)
import { AuthPageComponent } from '../app/auth-page.component';
import { SignupPageComponent } from './signup-page.component';
import { DashboardComponent } from './dashboard.component';
import { ProjectDetailComponent } from './project-detail/project-detail.component';

export const routes: Routes = [
  { path: 'login', component: AuthPageComponent },
  { path: 'signup', component: SignupPageComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'projects/:id', component: ProjectDetailComponent },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
