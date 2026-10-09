import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest } from '../models/auth.model';

const TOKEN_KEY = 'access_token';
const REFRESH_KEY = 'refresh_token';
const USER_KEY = 'auth_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = environment.apiUrl;

  currentUser = signal<AuthResponse | null>(this.loadUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, req).pipe(
      tap(res => {
        localStorage.setItem(TOKEN_KEY, res.accessToken);
        localStorage.setItem(REFRESH_KEY, res.refreshToken);
        localStorage.setItem(USER_KEY, JSON.stringify(res));
        this.currentUser.set(res);
      })
    );
  }

  refresh() {
    const token = localStorage.getItem(REFRESH_KEY);
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/auth/refresh`, { refreshToken: token })
      .pipe(
        tap(res => {
          localStorage.setItem(TOKEN_KEY, res.accessToken);
          localStorage.setItem(REFRESH_KEY, res.refreshToken);
          localStorage.setItem(USER_KEY, JSON.stringify(res));
          this.currentUser.set(res);
        })
      );
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private loadUser(): AuthResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
