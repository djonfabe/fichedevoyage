export type RoleUser = 'ADMIN' | 'SUPERVISEUR' | 'AGENT_FRONTIERE';

export interface UserResponse {
  id: number;
  email: string;
  nom: string;
  prenoms: string;
  role: RoleUser;
  active: boolean;
  createdAt: string;
}

export interface UserCreateRequest {
  email: string;
  password: string;
  nom: string;
  prenoms: string;
  role: RoleUser;
}
