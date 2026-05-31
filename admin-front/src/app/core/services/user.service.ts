import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UserCreateRequest, UserResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<UserResponse[]>(`${this.apiUrl}/users`);
  }

  create(req: UserCreateRequest) {
    return this.http.post<UserResponse>(`${this.apiUrl}/users`, req);
  }

  toggleActive(id: number, active: boolean) {
    return this.http.patch<UserResponse>(`${this.apiUrl}/users/${id}/active`, { active });
  }
}
