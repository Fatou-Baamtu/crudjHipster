import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

const produitResolve = (route: ActivatedRouteSnapshot): Observable<null | IProduit> => {
  const id = route.params.id;
  if (id) {
    return inject(ProduitService)
      .find(id)
      .pipe(
        mergeMap((produit: HttpResponse<IProduit>) => {
          if (produit.body) {
            return of(produit.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default produitResolve;
