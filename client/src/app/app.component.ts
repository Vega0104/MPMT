// File: client/src/app/app.component.ts
import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, RouterLink, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  showHomeButton = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.updateHomeButtonVisibility(this.router.url);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => this.updateHomeButtonVisibility(event.urlAfterRedirects));
  }

  private updateHomeButtonVisibility(url: string) {
    const token = localStorage.getItem('auth_token');
    const isDashboard = url.startsWith('/dashboard');
    const isAuthPage = url.startsWith('/login') || url.startsWith('/signup');

    this.showHomeButton = !!token && !isDashboard && !isAuthPage;
  }
}
