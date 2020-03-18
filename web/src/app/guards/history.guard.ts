import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { catchError, map } from 'rxjs/operators';
import { ERROR_TYPES } from '../models/errors';
import { ErrorsService } from '../services/errors/errors.service';

@Injectable({
  providedIn: 'root'
})
export class HistoryGuard implements CanActivate {
  constructor(
    private taskanaEngineService: TaskanaEngineService,
    public router: Router,
    private errorsService: ErrorsService
  ) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.taskanaEngineService.isHistoryProviderEnabled().pipe(
      map(value => {
        if (value) {
          return value;
        }
        return this.navigateToWorkplace();
      }),
      catchError(() => {
        this.errorsService.updateError(ERROR_TYPES.FETCH_ERR_6);
        return of(this.navigateToWorkplace());
      })
    );
  }

  navigateToWorkplace(): boolean {
    this.router.navigate(['workplace']);
    return false;
  }
}
