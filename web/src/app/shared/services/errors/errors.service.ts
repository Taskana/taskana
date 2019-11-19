import { ErrorHandler, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { ErrorModel } from '../../../models/error-model';
import { ERROR_TYPES } from '../../../services/general-modal/errors';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  // Wie initialisieren? Default ERROR_TYPE für leeres initialisieren einfügen?
  errorSubject$: Subject<ErrorModel>;
  constructor() {}

  private updateErrorSubject(errorToShow: ErrorModel) {
    this.errorSubject$.next(errorToShow);
  }

  public updateError(key: ERROR_TYPES, passedError: ErrorHandler): void {
    // wahrscheinlich wollen wir nicht jedes mal ein neues ErrorModel erzeugen... oder wollen wir?
    this.updateErrorSubject(new ErrorModel(key, passedError));
  }
}
