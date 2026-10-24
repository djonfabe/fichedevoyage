export interface CompagnieResponse {
  id: number;
  nom: string;
  codeIata: string;
  codeIcao: string;
  active: boolean;
}

export interface CompagnieRequest {
  nom: string;
  codeIata: string;
  codeIcao: string;
  active: boolean;
}
