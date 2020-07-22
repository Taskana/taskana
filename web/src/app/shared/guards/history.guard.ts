import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';
import { catchError, map } from 'rxjs/operators';
import { NOTIFICATION_TYPES } from '../models/notifications';
import { NotificationService } from '../services/notifications/notification.service';

@Injectable({
  providedIn: 'root'
})
export class HistoryGuard implements CanActivate {
  constructor(
    private taskanaEngineService: TaskanaEngineService,
    public router: Router,
    private errorsService: NotificationService
  ) {}

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
        this.errorsService.triggerError(NOTIFICATION_TYPES.FETCH_ERR_6);
        return of(this.navigateToWorkplace());
      })
    );
  }

  navigateToWorkplace(): boolean {
    this.router.navigate(['workplace']);
    return false;
  }
}
