import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'fiches',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/fiches/fiches.component').then(m => m.FichesComponent),
  },
  {
    path: 'compagnies',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/compagnies/compagnies.component').then(m => m.CompagniesComponent),
  },
  {
    path: 'users',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/users/users.component').then(m => m.UsersComponent),
  },
  { path: '**', redirectTo: '/dashboard' },
];
