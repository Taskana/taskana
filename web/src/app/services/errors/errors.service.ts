import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorModel } from '../../models/error-model';
import { ERROR_TYPES } from '../../models/errors';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  errorSubject$: Subject<ErrorModel> = new Subject<ErrorModel>();

  public updateError(key: ERROR_TYPES, passedError?: HttpErrorResponse, addition?: Map<String, String>): void {
    const errorModel = new ErrorModel(key, passedError, addition);
    this.updateErrorSubject(errorModel);
  }

  getError(): Observable<ErrorModel> {
    return this.errorSubject$.asObservable();
  }

  private updateErrorSubject(errorToShow: ErrorModel) {
    this.errorSubject$.next(errorToShow);
  }
}
