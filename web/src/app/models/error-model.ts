import { HttpErrorResponse } from '@angular/common/http';
import { ERROR_TYPES, errors } from './errors';

export class ErrorModel {
  public readonly errObj: HttpErrorResponse;
  public readonly title: string;
  public readonly message: string;

  constructor(key: ERROR_TYPES, passedError?: HttpErrorResponse, addition?: Map<String, String>) {
    this.title = errors.get(key).name;
    this.message = errors.get(key).text;
    this.errObj = passedError;
    if (addition) {
      addition.forEach((value: string, replacementKey: string) => {
        this.message.replace(`{${replacementKey}}`, value);
      });
    }
  }
}
