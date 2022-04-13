import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HistoryGuard implements CanActivate {
  constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.taskanaEngineService.isHistoryProviderEnabled().pipe(
      map((value) => {
        if (value) {
          return value;
        }
        return this.router.parseUrl('/taskana/workplace');
      }),
      catchError(() => {
        return of(this.router.parseUrl('/taskana/workplace'));
      })
    );
  }
}
