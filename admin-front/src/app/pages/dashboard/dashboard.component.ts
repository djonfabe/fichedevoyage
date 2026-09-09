import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { SidebarComponent } from '../../shared/components/sidebar.component';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardStats } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [SidebarComponent, MatCardModule, MatProgressBarModule],
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px,1fr)); gap: 16px; }
    .stat-value { font-size: 2.25rem; font-weight: 700; color: #0f172a; margin: 8px 0 0; }
    .stat-label { font-size: 0.8rem; color: #64748b; }
  `],
  template: `
    <div class="admin-layout">
      <app-sidebar />
      <main class="admin-main">
        <div class="page-header">
          <h1 class="page-title">Tableau de bord</h1>
          <p class="page-subtitle">Vue d'ensemble de l'activité</p>
        </div>

        @if (loading()) {
          <mat-progress-bar mode="indeterminate"></mat-progress-bar>
        } @else if (error()) {
          <mat-card><mat-card-content>{{ error() }}</mat-card-content></mat-card>
        } @else if (stats()) {
          <div class="stats-grid">
            <mat-card>
              <mat-card-content>
                <div class="stat-label">Total fiches</div>
                <div class="stat-value">{{ stats()!.fichesTotal }}</div>
              </mat-card-content>
            </mat-card>
            <mat-card style="background:#eff6ff;border-color:#bfdbfe">
              <mat-card-content>
                <div class="stat-label" style="color:#1d4ed8">Aujourd'hui</div>
                <div class="stat-value" style="color:#1e40af">{{ stats()!.fichesAujourdhui }}</div>
              </mat-card-content>
            </mat-card>
            <mat-card style="background:#eef2ff;border-color:#c7d2fe">
              <mat-card-content>
                <div class="stat-label" style="color:#4338ca">Ce mois</div>
                <div class="stat-value" style="color:#3730a3">{{ stats()!.fichesMoisEnCours }}</div>
              </mat-card-content>
            </mat-card>
            <mat-card style="background:#fefce8;border-color:#fde68a">
              <mat-card-content>
                <div class="stat-label" style="color:#92400e">En attente</div>
                <div class="stat-value" style="color:#78350f">{{ stats()!.fichesBrouillon }}</div>
              </mat-card-content>
            </mat-card>
            <mat-card style="background:#f0fdf4;border-color:#bbf7d0">
              <mat-card-content>
                <div class="stat-label" style="color:#15803d">Validées</div>
                <div class="stat-value" style="color:#14532d">{{ stats()!.fichesValidees }}</div>
              </mat-card-content>
            </mat-card>
            <mat-card style="background:#faf5ff;border-color:#e9d5ff">
              <mat-card-content>
                <div class="stat-label" style="color:#7e22ce">Scannées auj.</div>
                <div class="stat-value" style="color:#6b21a8">{{ stats()!.fichesScanneesAujourdhui }}</div>
              </mat-card-content>
            </mat-card>
          </div>
        }
      </main>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  stats = signal<DashboardStats | null>(null);
  loading = signal(true);
  error = signal('');

  ngOnInit() {
    this.dashboardService.getStats().subscribe({
      next: d => { this.stats.set(d); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les statistiques.'); this.loading.set(false); },
    });
  }
}
