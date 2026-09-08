import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
  ],
  styles: [`
    :host { display: flex; align-items: center; justify-content: center;
            min-height: 100vh; background: #f1f5f9; padding: 16px; }
    mat-card { width: 100%; max-width: 400px; }
    .logo-row { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
    .logo-row mat-icon { font-size: 36px; width: 36px; height: 36px; color: #3b82f6; }
    h1 { font-size: 1.25rem; font-weight: 700; margin: 0; }
    .subtitle { font-size: 0.875rem; color: #64748b; margin: 4px 0 24px; }
    .error-box { background: #fef2f2; border: 1px solid #fecaca; color: #b91c1c;
                 border-radius: 8px; padding: 10px 14px; font-size: 0.875rem; margin-bottom: 16px; }
    mat-form-field { width: 100%; }
    .submit-row { margin-top: 8px; }
    button[mat-raised-button] { width: 100%; }
    .spinner-wrap { display: flex; align-items: center; justify-content: center; gap: 8px; }
  `],
  template: `
    <mat-card>
      <mat-card-content style="padding: 32px">
        <div class="logo-row">
          <mat-icon>flight</mat-icon>
          <h1>Fiche de Voyage BF</h1>
        </div>
        <p class="subtitle">Interface d'administration</p>

        @if (error()) {
          <div class="error-box">{{ error() }}</div>
        }

        <form [formGroup]="form" (ngSubmit)="submit()">
          <mat-form-field appearance="outline">
            <mat-label>Adresse email</mat-label>
            <input matInput type="email" formControlName="email" autocomplete="email"/>
            <mat-icon matSuffix>email</mat-icon>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Mot de passe</mat-label>
            <input matInput [type]="showPwd() ? 'text' : 'password'"
                   formControlName="password" autocomplete="current-password"/>
            <button mat-icon-button matSuffix type="button" (click)="showPwd.update(v => !v)">
              <mat-icon>{{ showPwd() ? 'visibility_off' : 'visibility' }}</mat-icon>
            </button>
          </mat-form-field>

          <div class="submit-row">
            <button mat-raised-button color="primary" type="submit"
                    [disabled]="form.invalid || loading()">
              @if (loading()) {
                <span class="spinner-wrap">
                  <mat-spinner diameter="18" strokeWidth="2"></mat-spinner>
                  Connexion...
                </span>
              } @else {
                Se connecter
              }
            </button>
          </div>
        </form>
      </mat-card-content>
    </mat-card>
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  error = signal('');
  showPwd = signal(false);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set('');
    const { email, password } = this.form.value;
    this.auth.login({ email: email!, password: password! }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        this.loading.set(false);
        this.error.set(
          err.status === 401 ? 'Email ou mot de passe incorrect.' : 'Erreur de connexion.'
        );
      },
    });
  }
}
