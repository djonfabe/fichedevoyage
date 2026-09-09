import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CompagnieRequest, CompagnieResponse } from '../models/compagnie.model';

@Injectable({ providedIn: 'root' })
export class CompagnieService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<CompagnieResponse[]>(`${this.apiUrl}/compagnies`);
  }

  create(req: CompagnieRequest) {
    return this.http.post<CompagnieResponse>(`${this.apiUrl}/compagnies`, req);
  }

  update(id: number, req: CompagnieRequest) {
    return this.http.put<CompagnieResponse>(`${this.apiUrl}/compagnies/${id}`, req);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/compagnies/${id}`);
  }
}
