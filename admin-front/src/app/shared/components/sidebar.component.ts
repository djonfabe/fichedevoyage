import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatIconModule],
  styles: [`
    :host { display: flex; flex-direction: column; width: 240px; min-height: 100vh;
            background: #1e293b; color: #f1f5f9; flex-shrink: 0; }
    .brand { display: flex; align-items: center; gap: 10px; padding: 20px 16px;
             border-bottom: 1px solid rgba(255,255,255,0.08); }
    .brand-name { font-weight: 700; font-size: 1rem; line-height: 1.2; }
    .brand-sub { font-size: 0.7rem; color: #94a3b8; }
    nav { flex: 1; padding: 12px 8px; }
    .user-section { padding: 12px 8px; border-top: 1px solid rgba(255,255,255,0.08); }
    .user-info { padding: 8px 12px 4px; }
    .user-name { font-size: 0.875rem; font-weight: 600; }
    .user-role { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }
  `],
  template: `
    <div class="brand">
      <mat-icon style="color:#3b82f6;font-size:28px;width:28px;height:28px">flight</mat-icon>
      <div>
        <div class="brand-name">Fiche de Voyage</div>
        <div class="brand-sub">Administration</div>
      </div>
    </div>

    <nav>
      <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
        <mat-icon>dashboard</mat-icon> Tableau de bord
      </a>
      <a routerLink="/fiches" routerLinkActive="active" class="nav-item">
        <mat-icon>article</mat-icon> Fiches de voyage
      </a>
      <a routerLink="/compagnies" routerLinkActive="active" class="nav-item">
        <mat-icon>flight_takeoff</mat-icon> Compagnies
      </a>
      @if (isAdmin()) {
        <a routerLink="/users" routerLinkActive="active" class="nav-item">
          <mat-icon>manage_accounts</mat-icon> Utilisateurs
        </a>
      }
    </nav>

    <div class="user-section">
      <div class="user-info">
        <div class="user-name">{{ user()?.prenoms }} {{ user()?.nom }}</div>
        <div class="user-role">{{ roleLabel() }}</div>
      </div>
      <button (click)="logout()" class="nav-item" style="width:100%;background:none;border:none;color:#f87171">
        <mat-icon>logout</mat-icon> Déconnexion
      </button>
    </div>
  `,
})
export class SidebarComponent {
  private auth = inject(AuthService);
  user = this.auth.currentUser;

  isAdmin() { return this.user()?.role === 'ADMIN'; }

  roleLabel() {
    const map: Record<string, string> = {
      ADMIN: 'Administrateur', SUPERVISEUR: 'Superviseur', AGENT_FRONTIERE: 'Agent frontière',
    };
    return map[this.user()?.role ?? ''] ?? this.user()?.role;
  }

  logout() { this.auth.logout(); }
}
