import { ErrorHandler } from '@angular/core';
import { ERROR_TYPES, errors } from '../services/general-modal/errors';

export class ErrorModel {
  head: string;
  body: string;
  errObj?: ErrorHandler;

  constructor(key: ERROR_TYPES, passedError?: ErrorHandler, addition?: string) {
    this.head = errors.get(key).name;
    this.body = errors.get(key).text
    if (addition) {
      this.body.replace('{rep}', addition);
    }
    this.errObj = passedError;
  }
}
