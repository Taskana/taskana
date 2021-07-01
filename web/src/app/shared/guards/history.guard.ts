import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HistoryGuard implements CanActivate {
  constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.taskanaEngineService.isHistoryProviderEnabled().pipe(
      map((value) => {
        if (value) {
          return value;
        }
        return this.navigateToWorkplace();
      }),
      catchError(() => {
        return of(this.navigateToWorkplace());
      })
    );
  }

  navigateToWorkplace(): boolean {
    this.router.navigate(['workplace']);
    return false;
  }
}
