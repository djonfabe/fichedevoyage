import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { SidebarComponent } from '../../shared/components/sidebar.component';
import { UserService } from '../../core/services/user.service';
import { RoleUser, UserResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    SidebarComponent, ReactiveFormsModule, DatePipe,
    MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatProgressBarModule, MatCardModule,
  ],
  styles: [`
    .page-actions { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
    .overlay { position:fixed;inset:0;background:rgba(0,0,0,.4);display:flex;
               align-items:center;justify-content:center;z-index:1000;padding:16px; }
    .dialog { width:100%;max-width:460px; }
    .dialog-title { font-size:1.1rem;font-weight:600;margin:0 0 20px; }
    .row2 { display:grid;grid-template-columns:1fr 1fr;gap:12px; }
    .form-actions { display:flex;gap:8px;justify-content:flex-end;margin-top:8px; }
    .error-box { background:#fef2f2;border:1px solid #fecaca;color:#b91c1c;
                 border-radius:8px;padding:10px 14px;font-size:0.875rem;margin-bottom:16px; }
    mat-form-field { width:100%; }
  `],
  template: `
    <div class="admin-layout">
      <app-sidebar />
      <main class="admin-main">
        <div class="page-actions">
          <div>
            <h1 class="page-title">Utilisateurs</h1>
            <p class="page-subtitle">Gestion des comptes administrateurs</p>
          </div>
          <button mat-raised-button color="primary" (click)="openCreate()">
            <mat-icon>person_add</mat-icon> Nouvel utilisateur
          </button>
        </div>

        @if (loading()) { <mat-progress-bar mode="indeterminate" style="margin-bottom:8px"></mat-progress-bar> }

        <mat-card>
          <mat-table [dataSource]="users()">
            <ng-container matColumnDef="nom">
              <mat-header-cell *matHeaderCellDef>Nom</mat-header-cell>
              <mat-cell *matCellDef="let u">{{ u.prenoms }} {{ u.nom }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="email">
              <mat-header-cell *matHeaderCellDef>Email</mat-header-cell>
              <mat-cell *matCellDef="let u">{{ u.email }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="role">
              <mat-header-cell *matHeaderCellDef>Rôle</mat-header-cell>
              <mat-cell *matCellDef="let u">
                <span [class]="'badge ' + roleClass(u.role)">{{ roleLabel(u.role) }}</span>
              </mat-cell>
            </ng-container>
            <ng-container matColumnDef="createdAt">
              <mat-header-cell *matHeaderCellDef>Créé le</mat-header-cell>
              <mat-cell *matCellDef="let u">{{ u.createdAt | date:'dd/MM/yyyy' }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="statut">
              <mat-header-cell *matHeaderCellDef>Statut</mat-header-cell>
              <mat-cell *matCellDef="let u">
                <span [class]="'badge ' + (u.active ? 'badge-green' : 'badge-gray')">
                  {{ u.active ? 'Actif' : 'Inactif' }}
                </span>
              </mat-cell>
            </ng-container>
            <ng-container matColumnDef="actions">
              <mat-header-cell *matHeaderCellDef>Actions</mat-header-cell>
              <mat-cell *matCellDef="let u">
                <button [class]="'action-btn ' + (u.active ? 'action-btn-red' : 'action-btn-green')"
                        (click)="toggleActive(u)">
                  {{ u.active ? 'Désactiver' : 'Activer' }}
                </button>
              </mat-cell>
            </ng-container>
            <mat-header-row *matHeaderRowDef="cols"></mat-header-row>
            <mat-row *matRowDef="let r; columns: cols"></mat-row>
          </mat-table>
        </mat-card>
      </main>
    </div>

    @if (showModal()) {
      <div class="overlay">
        <mat-card class="dialog">
          <mat-card-content style="padding:24px">
            <h3 class="dialog-title">Nouvel utilisateur</h3>

            @if (formError()) {
              <div class="error-box">{{ formError() }}</div>
            }

            <form [formGroup]="form" (ngSubmit)="submit()">
              <div class="row2">
                <mat-form-field appearance="outline">
                  <mat-label>Nom</mat-label>
                  <input matInput formControlName="nom"/>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Prénoms</mat-label>
                  <input matInput formControlName="prenoms"/>
                </mat-form-field>
              </div>
              <mat-form-field appearance="outline">
                <mat-label>Email</mat-label>
                <input matInput type="email" formControlName="email"/>
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Mot de passe</mat-label>
                <input matInput type="password" formControlName="password"/>
                <mat-hint>Minimum 8 caractères</mat-hint>
              </mat-form-field>
              <mat-form-field appearance="outline" style="margin-top:8px">
                <mat-label>Rôle</mat-label>
                <mat-select formControlName="role">
                  <mat-option value="ADMIN">Administrateur</mat-option>
                  <mat-option value="SUPERVISEUR">Superviseur</mat-option>
                  <mat-option value="AGENT_FRONTIERE">Agent frontière</mat-option>
                </mat-select>
              </mat-form-field>

              <div class="form-actions">
                <button mat-stroked-button type="button" (click)="showModal.set(false)">Annuler</button>
                <button mat-raised-button color="primary" type="submit"
                        [disabled]="form.invalid || saving()">
                  {{ saving() ? 'Création...' : 'Créer' }}
                </button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    }
  `,
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  cols = ['nom', 'email', 'role', 'createdAt', 'statut', 'actions'];
  users = signal<UserResponse[]>([]);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);
  formError = signal('');

  form = this.fb.group({
    nom: ['', Validators.required],
    prenoms: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['SUPERVISEUR' as RoleUser, Validators.required],
  });

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.userService.getAll().subscribe({
      next: d => { this.users.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.form.reset({ role: 'SUPERVISEUR' });
    this.formError.set('');
    this.showModal.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    this.saving.set(true);
    this.userService.create(this.form.value as any).subscribe({
      next: () => { this.showModal.set(false); this.saving.set(false); this.load(); },
      error: err => { this.saving.set(false); this.formError.set(err.error?.message ?? 'Erreur.'); },
    });
  }

  toggleActive(u: UserResponse) {
    this.userService.toggleActive(u.id, !u.active).subscribe(() => this.load());
  }

  roleLabel(r: RoleUser) {
    return { ADMIN: 'Admin', SUPERVISEUR: 'Superviseur', AGENT_FRONTIERE: 'Agent' }[r] ?? r;
  }
  roleClass(r: RoleUser) {
    return { ADMIN: 'badge-purple', SUPERVISEUR: 'badge-blue', AGENT_FRONTIERE: 'badge-orange' }[r] ?? '';
  }
}
