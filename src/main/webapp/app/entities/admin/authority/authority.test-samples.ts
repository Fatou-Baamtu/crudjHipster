import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '2337ba20-e90a-4299-8b30-677c2bc43eb3',
};

export const sampleWithPartialData: IAuthority = {
  name: '639aa676-2a90-4d4e-a13e-b6cfe5bcbfca',
};

export const sampleWithFullData: IAuthority = {
  name: '136a60e0-5cd1-45a4-8d21-f36ffc8792b2',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
