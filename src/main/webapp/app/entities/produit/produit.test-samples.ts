import { IProduit, NewProduit } from './produit.model';

export const sampleWithRequiredData: IProduit = {
  id: 10294,
  prix: 'pourvu que',
  quantite: 8647,
};

export const sampleWithPartialData: IProduit = {
  id: 24169,
  nom: 'serviable',
  prix: 'malade',
  quantite: 5714,
};

export const sampleWithFullData: IProduit = {
  id: 21631,
  nom: 'personnel',
  prix: 'comme envers lors',
  quantite: 26689,
};

export const sampleWithNewData: NewProduit = {
  prix: 'au prix de',
  quantite: 23728,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
