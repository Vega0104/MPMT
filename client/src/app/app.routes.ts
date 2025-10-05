import { Routes } from '@angular/router';
import {AuthPageComponent} from "./auth-page.component";
import {SignupPageComponent} from "./signup-page.component";
import {DashboardComponent} from "./dashboard.component";
import {ProjectDetailComponent} from "./project-detail/project-detail.component";

export const routes: Routes = [
  // app-routing.module.ts
  { path: 'login', component: AuthPageComponent},
  { path: 'signup', component: SignupPageComponent},
  { path: 'dashboard', component: DashboardComponent},
  { path: 'projects/:id', component: ProjectDetailComponent},
  { path: '**', redirectTo: 'login' }

];
