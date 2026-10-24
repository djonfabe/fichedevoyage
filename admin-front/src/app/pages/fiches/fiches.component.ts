import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { SidebarComponent } from '../../shared/components/sidebar.component';
import { FicheService } from '../../core/services/fiche.service';
import { FicheVoyage, StatutFiche } from '../../core/models/fiche.model';

@Component({
  selector: 'app-fiches',
  standalone: true,
  imports: [
    SidebarComponent, FormsModule, DatePipe,
    MatTableModule, MatSelectModule, MatFormFieldModule,
    MatButtonModule, MatIconModule, MatProgressBarModule,
    MatDialogModule, MatCardModule, MatPaginatorModule,
  ],
  styles: [`
    .toolbar { display: flex; align-items: center; gap: 16px; margin-bottom: 16px; }
    .toolbar mat-form-field { width: 200px; }
    .ref { font-family: monospace; font-size: 0.8rem; color: #1d4ed8; }
    .sub { font-size: 0.75rem; color: #94a3b8; }
    .actions { display: flex; gap: 6px; }
    mat-table { width: 100%; }
  `],
  template: `
    <div class="admin-layout">
      <app-sidebar />
      <main class="admin-main">
        <div class="page-header">
          <h1 class="page-title">Fiches de voyage</h1>
          <p class="page-subtitle">Gestion des pré-inscriptions</p>
        </div>

        <div class="toolbar">
          <mat-form-field appearance="outline" subscriptSizing="dynamic">
            <mat-label>Statut</mat-label>
            <mat-select [(ngModel)]="selectedStatut" (ngModelChange)="onStatutChange()">
              <mat-option value="">Tous</mat-option>
              <mat-option value="BROUILLON">En attente</mat-option>
              <mat-option value="VALIDEE">Validée</mat-option>
              <mat-option value="SCANNEE">Scannée</mat-option>
              <mat-option value="ANNULEE">Annulée</mat-option>
            </mat-select>
          </mat-form-field>
        </div>

        @if (loading()) { <mat-progress-bar mode="indeterminate" style="margin-bottom:8px"></mat-progress-bar> }

        <mat-card>
          <mat-table [dataSource]="fiches()">
            <ng-container matColumnDef="reference">
              <mat-header-cell *matHeaderCellDef>Référence</mat-header-cell>
              <mat-cell *matCellDef="let f"><span class="ref">{{ f.reference }}</span></mat-cell>
            </ng-container>

            <ng-container matColumnDef="voyageur">
              <mat-header-cell *matHeaderCellDef>Voyageur</mat-header-cell>
              <mat-cell *matCellDef="let f">
                <div>{{ f.nom }} {{ f.prenoms }}</div>
                <div class="sub">{{ f.email }}</div>
              </mat-cell>
            </ng-container>

            <ng-container matColumnDef="nationalite">
              <mat-header-cell *matHeaderCellDef>Nationalité</mat-header-cell>
              <mat-cell *matCellDef="let f">{{ f.nationalite?.nomFr }}</mat-cell>
            </ng-container>

            <ng-container matColumnDef="dateVoyage">
              <mat-header-cell *matHeaderCellDef>Date voyage</mat-header-cell>
              <mat-cell *matCellDef="let f">{{ f.dateVoyage | date:'dd/MM/yyyy' }}</mat-cell>
            </ng-container>

            <ng-container matColumnDef="vol">
              <mat-header-cell *matHeaderCellDef>Vol</mat-header-cell>
              <mat-cell *matCellDef="let f">
                <div>{{ f.compagnie?.nom }}</div>
                <div class="sub">{{ f.numeroVol }}</div>
              </mat-cell>
            </ng-container>

            <ng-container matColumnDef="statut">
              <mat-header-cell *matHeaderCellDef>Statut</mat-header-cell>
              <mat-cell *matCellDef="let f">
                <span [class]="'badge ' + statutClass(f.statut)">{{ statutLabel(f.statut) }}</span>
              </mat-cell>
            </ng-container>

            <ng-container matColumnDef="actions">
              <mat-header-cell *matHeaderCellDef>Actions</mat-header-cell>
              <mat-cell *matCellDef="let f">
                @if (f.statut === 'BROUILLON') {
                  <div class="actions">
                    <button class="action-btn action-btn-green" (click)="valider(f)">Valider</button>
                    <button class="action-btn action-btn-red" (click)="confirmAnnuler(f)">Annuler</button>
                  </div>
                }
              </mat-cell>
            </ng-container>

            <mat-header-row *matHeaderRowDef="cols"></mat-header-row>
            <mat-row *matRowDef="let row; columns: cols"></mat-row>

            <tr class="mat-row" *matNoDataRow>
              <td [attr.colspan]="cols.length" style="padding:32px;text-align:center;color:#94a3b8">
                Aucune fiche trouvée.
              </td>
            </tr>
          </mat-table>

          <mat-paginator [length]="totalElements()" [pageSize]="20"
                         [pageSizeOptions]="[10,20,50]"
                         (page)="onPage($event)">
          </mat-paginator>
        </mat-card>
      </main>
    </div>

    @if (annulerFiche()) {
      <div style="position:fixed;inset:0;background:rgba(0,0,0,.4);display:flex;
                  align-items:center;justify-content:center;z-index:1000;padding:16px">
        <mat-card style="max-width:420px;width:100%">
          <mat-card-content style="padding:24px">
            <h3 style="margin:0 0 12px;font-size:1.1rem;font-weight:600">Confirmer l'annulation</h3>
            <p style="color:#475569;font-size:0.875rem;margin:0 0 24px">
              Annuler la fiche <strong>{{ annulerFiche()!.reference }}</strong> de
              {{ annulerFiche()!.nom }} {{ annulerFiche()!.prenoms }} ?
            </p>
            <div style="display:flex;gap:8px;justify-content:flex-end">
              <button mat-stroked-button (click)="annulerFiche.set(null)">Non</button>
              <button mat-raised-button color="warn" (click)="confirmerAnnulation()">Oui, annuler</button>
            </div>
          </mat-card-content>
        </mat-card>
      </div>
    }
  `,
})
export class FichesComponent implements OnInit {
  private ficheService = inject(FicheService);

  cols = ['reference', 'voyageur', 'nationalite', 'dateVoyage', 'vol', 'statut', 'actions'];
  fiches = signal<FicheVoyage[]>([]);
  loading = signal(true);
  page = signal(0);
  totalElements = signal(0);
  selectedStatut = '';
  annulerFiche = signal<FicheVoyage | null>(null);

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.ficheService.getFiches(this.page(), 20, this.selectedStatut as StatutFiche || undefined).subscribe({
      next: p => { this.fiches.set(p.content); this.totalElements.set(p.totalElements); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  onStatutChange() { this.page.set(0); this.load(); }

  onPage(e: PageEvent) { this.page.set(e.pageIndex); this.load(); }

  valider(f: FicheVoyage) { this.ficheService.valider(f.id).subscribe(() => this.load()); }

  confirmAnnuler(f: FicheVoyage) { this.annulerFiche.set(f); }

  confirmerAnnulation() {
    const f = this.annulerFiche();
    if (!f) return;
    this.ficheService.annuler(f.id).subscribe(() => { this.annulerFiche.set(null); this.load(); });
  }

  statutLabel(s: StatutFiche) {
    return { BROUILLON: 'En attente', VALIDEE: 'Validée', SCANNEE: 'Scannée', ANNULEE: 'Annulée' }[s] ?? s;
  }
  statutClass(s: StatutFiche) {
    return { BROUILLON: 'badge-yellow', VALIDEE: 'badge-green', SCANNEE: 'badge-blue', ANNULEE: 'badge-gray' }[s] ?? '';
  }
}
