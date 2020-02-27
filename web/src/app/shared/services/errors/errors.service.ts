import { ErrorHandler, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { ErrorModel } from '../../../models/error-model';
import { ERROR_TYPES } from '../../../services/general-modal/errors';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  errorSubject$: Subject<ErrorModel>;
  constructor() {}

  private updateErrorSubject(errorToShow: ErrorModel) {
    this.errorSubject$.next(errorToShow);
  }

  public updateError(key: ERROR_TYPES, passedError?: ErrorHandler): void {
    const errorModel = new ErrorModel(key, passedError);
    this.updateErrorSubject(errorModel);
  }
}
