export interface IProduit {
  id: number;
  nom?: string | null;
  prix?: string | null;
  quantite?: number | null;
}

export type NewProduit = Omit<IProduit, 'id'> & { id: null };
