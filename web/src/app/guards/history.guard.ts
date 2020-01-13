import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { map, catchError } from 'rxjs/operators';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { MessageModal } from 'app/models/message-modal';

@Injectable({
  providedIn: 'root'
})
export class HistoryGuard implements CanActivate {
  constructor(
    private taskanaEngineService: TaskanaEngineService,
    public router: Router,
    public generalModalService: GeneralModalService
  ) { }

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
        this.generalModalService.triggerMessage(new MessageModal(
          'There was an error, please contact with your administrator', 'There was an error getting history provider'
        ));
        return of(this.navigateToWorkplace());
      })
    );
  }

  navigateToWorkplace(): boolean {
    this.router.navigate(['workplace']);
    return false;
  }
}
