import { Routes } from '@angular/router';
import {AuthPageComponent} from "./auth-page.component";
import {SignupPageComponent} from "./signup-page.component";
import {DashboardComponent} from "./dashboard.component";

export const routes: Routes = [
  // app-routing.module.ts
  { path: 'login', component: AuthPageComponent },
  { path: 'signup', component: SignupPageComponent },
  { path: 'dash', component: DashboardComponent },

];
