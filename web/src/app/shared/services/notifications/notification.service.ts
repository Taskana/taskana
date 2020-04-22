import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorModel } from '../../models/error-model';
import { NOTIFICATION_TYPES } from '../../models/notifications';
import { AlertModel } from '../../models/alert-model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  errorSubject$: Subject<ErrorModel> = new Subject<ErrorModel>();

  alertSubject$: Subject<AlertModel> = new Subject<AlertModel>();

  public triggerError(key: NOTIFICATION_TYPES, passedError?: HttpErrorResponse, addition?: Map<String, String>): void {
    const errorModel = new ErrorModel(key, passedError, addition);
    this.updateErrorSubject(errorModel);
  }

  getError(): Observable<ErrorModel> {
    return this.errorSubject$.asObservable();
  }

  protected updateErrorSubject(errorToShow: ErrorModel) {
    this.errorSubject$.next(errorToShow);
  }

  triggerAlert(key: NOTIFICATION_TYPES, additions?: Map<string, string>) {
    const alert: AlertModel = new AlertModel(key, additions);
    this.alertSubject$.next(alert);
  }

  getAlert(): Observable<AlertModel> {
    return this.alertSubject$.asObservable();
  }
}
