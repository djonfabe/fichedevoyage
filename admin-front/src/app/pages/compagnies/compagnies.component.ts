import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { SidebarComponent } from '../../shared/components/sidebar.component';
import { CompagnieService } from '../../core/services/compagnie.service';
import { CompagnieResponse } from '../../core/models/compagnie.model';

@Component({
  selector: 'app-compagnies',
  standalone: true,
  imports: [
    SidebarComponent, ReactiveFormsModule,
    MatTableModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatCheckboxModule,
    MatProgressBarModule, MatCardModule,
  ],
  styles: [`
    .page-actions { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
    .actions { display:flex; gap:6px; }
    .overlay { position:fixed;inset:0;background:rgba(0,0,0,.4);display:flex;
               align-items:center;justify-content:center;z-index:1000;padding:16px; }
    .dialog { width:100%;max-width:440px; }
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
            <h1 class="page-title">Compagnies aériennes</h1>
            <p class="page-subtitle">Gestion des compagnies</p>
          </div>
          <button mat-raised-button color="primary" (click)="openCreate()">
            <mat-icon>add</mat-icon> Nouvelle compagnie
          </button>
        </div>

        @if (loading()) { <mat-progress-bar mode="indeterminate" style="margin-bottom:8px"></mat-progress-bar> }

        <mat-card>
          <mat-table [dataSource]="compagnies()">
            <ng-container matColumnDef="nom">
              <mat-header-cell *matHeaderCellDef>Nom</mat-header-cell>
              <mat-cell *matCellDef="let c">{{ c.nom }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="codeIata">
              <mat-header-cell *matHeaderCellDef>IATA</mat-header-cell>
              <mat-cell *matCellDef="let c" style="font-family:monospace">{{ c.codeIata }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="codeIcao">
              <mat-header-cell *matHeaderCellDef>ICAO</mat-header-cell>
              <mat-cell *matCellDef="let c" style="font-family:monospace">{{ c.codeIcao }}</mat-cell>
            </ng-container>
            <ng-container matColumnDef="statut">
              <mat-header-cell *matHeaderCellDef>Statut</mat-header-cell>
              <mat-cell *matCellDef="let c">
                <span [class]="'badge ' + (c.active ? 'badge-green' : 'badge-gray')">
                  {{ c.active ? 'Active' : 'Inactive' }}
                </span>
              </mat-cell>
            </ng-container>
            <ng-container matColumnDef="actions">
              <mat-header-cell *matHeaderCellDef>Actions</mat-header-cell>
              <mat-cell *matCellDef="let c">
                <div class="actions">
                  <button class="action-btn action-btn-blue" (click)="openEdit(c)">Modifier</button>
                  <button class="action-btn action-btn-red" (click)="delete(c)">Supprimer</button>
                </div>
              </mat-cell>
            </ng-container>
            <mat-header-row *matHeaderRowDef="cols"></mat-header-row>
            <mat-row *matRowDef="let r; columns: cols"></mat-row>
            <tr class="mat-row" *matNoDataRow>
              <td [attr.colspan]="cols.length" style="padding:32px;text-align:center;color:#94a3b8">
                Aucune compagnie.
              </td>
            </tr>
          </mat-table>
        </mat-card>
      </main>
    </div>

    @if (showModal()) {
      <div class="overlay">
        <mat-card class="dialog">
          <mat-card-content style="padding:24px">
            <h3 class="dialog-title">{{ editId() ? 'Modifier' : 'Nouvelle' }} compagnie</h3>

            @if (formError()) {
              <div class="error-box">{{ formError() }}</div>
            }

            <form [formGroup]="form" (ngSubmit)="submit()">
              <mat-form-field appearance="outline">
                <mat-label>Nom</mat-label>
                <input matInput formControlName="nom"/>
              </mat-form-field>

              <div class="row2">
                <mat-form-field appearance="outline">
                  <mat-label>Code IATA</mat-label>
                  <input matInput formControlName="codeIata" maxlength="2" style="text-transform:uppercase"/>
                  <mat-hint>2 lettres, ex: AF</mat-hint>
                </mat-form-field>
                <mat-form-field appearance="outline">
                  <mat-label>Code ICAO</mat-label>
                  <input matInput formControlName="codeIcao" maxlength="3" style="text-transform:uppercase"/>
                  <mat-hint>3 lettres, ex: AFR</mat-hint>
                </mat-form-field>
              </div>

              <mat-checkbox formControlName="active" style="margin:12px 0 8px">Active</mat-checkbox>

              <div class="form-actions">
                <button mat-stroked-button type="button" (click)="showModal.set(false)">Annuler</button>
                <button mat-raised-button color="primary" type="submit"
                        [disabled]="form.invalid || saving()">
                  {{ saving() ? 'Enregistrement...' : 'Enregistrer' }}
                </button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>
    }
  `,
})
export class CompagniesComponent implements OnInit {
  private compagnieService = inject(CompagnieService);
  private fb = inject(FormBuilder);

  cols = ['nom', 'codeIata', 'codeIcao', 'statut', 'actions'];
  compagnies = signal<CompagnieResponse[]>([]);
  loading = signal(true);
  showModal = signal(false);
  editId = signal<number | null>(null);
  saving = signal(false);
  formError = signal('');

  form = this.fb.group({
    nom: ['', Validators.required],
    codeIata: ['', [Validators.required, Validators.maxLength(2)]],
    codeIcao: ['', Validators.maxLength(3)],
    active: [true],
  });

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.compagnieService.getAll().subscribe({
      next: d => { this.compagnies.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  openCreate() {
    this.editId.set(null);
    this.form.reset({ active: true });
    this.formError.set('');
    this.showModal.set(true);
  }

  openEdit(c: CompagnieResponse) {
    this.editId.set(c.id);
    this.form.setValue({ nom: c.nom, codeIata: c.codeIata ?? '', codeIcao: c.codeIcao ?? '', active: c.active });
    this.formError.set('');
    this.showModal.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    this.saving.set(true);
    const req = this.form.value as any;
    const id = this.editId();
    (id ? this.compagnieService.update(id, req) : this.compagnieService.create(req)).subscribe({
      next: () => { this.showModal.set(false); this.saving.set(false); this.load(); },
      error: err => { this.saving.set(false); this.formError.set(err.error?.message ?? 'Erreur.'); },
    });
  }

  delete(c: CompagnieResponse) {
    if (!confirm(`Supprimer "${c.nom}" ?`)) return;
    this.compagnieService.delete(c.id).subscribe(() => this.load());
  }
}
