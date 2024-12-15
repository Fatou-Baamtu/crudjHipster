import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProduitResolve from './route/produit-routing-resolve.service';

const produitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/produit.component').then(m => m.ProduitComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/produit-detail.component').then(m => m.ProduitDetailComponent),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/produit-update.component').then(m => m.ProduitUpdateComponent),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/produit-update.component').then(m => m.ProduitUpdateComponent),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default produitRoute;
