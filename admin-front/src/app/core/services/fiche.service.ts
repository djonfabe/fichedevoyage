import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { FicheVoyage, PageResponse, StatutFiche } from '../models/fiche.model';

@Injectable({ providedIn: 'root' })
export class FicheService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getFiches(page = 0, size = 20, statut?: StatutFiche) {
    let params = new HttpParams().set('page', page).set('size', size);
    if (statut) params = params.set('statut', statut);
    return this.http.get<PageResponse<FicheVoyage>>(`${this.apiUrl}/fiches`, { params });
  }

  valider(id: string) {
    return this.http.patch<FicheVoyage>(`${this.apiUrl}/fiches/${id}/valider`, {});
  }

  annuler(id: string) {
    return this.http.patch<FicheVoyage>(`${this.apiUrl}/fiches/${id}/annuler`, {});
  }
}
