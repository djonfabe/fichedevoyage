export type StatutFiche = 'BROUILLON' | 'VALIDEE' | 'SCANNEE' | 'ANNULEE';

export interface PaysRef {
  id: number;
  nomFr: string;
  nomEn: string;
  code: string;
}

export interface CompagnieRef {
  id: number;
  nom: string;
  codeIata: string;
  codeIcao: string;
}

export interface FicheVoyage {
  id: string;
  reference: string;
  qrCodeToken: string;
  nom: string;
  prenoms: string;
  nomJeuneFille: string;
  dateNaissance: string;
  lieuNaissance: string;
  email: string;
  profession: string;
  nationalite?: PaysRef;
  paysResidence?: PaysRef;
  typeDocument: string;
  numeroDocument: string;
  dateDelivrance: string;
  paysProvenance?: PaysRef;
  villeProvenance: string;
  paysDestination?: PaysRef;
  villeDestination: string;
  dateVoyage: string;
  motifVoyage: string;
  dureeSejour: number;
  typeVoyage: string;
  compagnie?: CompagnieRef;
  numeroVol: string;
  statut: StatutFiche;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
