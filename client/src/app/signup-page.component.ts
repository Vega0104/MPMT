import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, SignupData } from './services/auth-service/auth-service.service';

@Component({
  selector: 'app-signup-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup-page.component.html'
})
export class SignupPageComponent {
  formData: SignupData & { confirmPassword?: string } = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    const { username, email, password, confirmPassword } = this.formData;

    if (password !== confirmPassword) {
      alert('Les mots de passe ne correspondent pas.');
      return;
    }

    this.authService.signup({ username, email, password }).subscribe({
      next: (res) => {
        // Enregistre le token si ton backend le renvoie (ajuste selon ta réponse API)
        if ('token' in res) {
          this.authService.saveToken?.(res.token);
        }
        this.router.navigate(['/login']).then(() => {
          console.log('Navigation vers /login réussie');
        });
      },
      error: (err) => {
        console.error(err);
        alert('Erreur lors de l’inscription.');
      }
    });
  }
}
