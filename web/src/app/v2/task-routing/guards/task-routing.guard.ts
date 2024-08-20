import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TaskRoutingGuard implements CanActivate {
  constructor(private kadaiEngineService: KadaiEngineService, public router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.kadaiEngineService.isCustomRoutingRulesEnabled().pipe(
      map((value) => {
        if (value) {
          return value;
        }
        return this.router.parseUrl('/kadai/workplace');
      }),
      catchError(() => {
        return of(this.router.parseUrl('/kadai/workplace'));
      })
    );
  }
}
