import { HttpErrorResponse } from '@angular/common/http';
import { NOTIFICATION_TYPES, notifications } from './notifications';

export class ErrorModel {
  public readonly errObj: HttpErrorResponse;
  public readonly title: string;
  public readonly message: string;

  constructor(key: NOTIFICATION_TYPES, passedError?: HttpErrorResponse, addition?: Map<String, String>) {
    this.title = notifications.get(key).left;
    let messageTemp = notifications.get(key).right;
    this.errObj = passedError;
    if (addition) {
      addition.forEach((value: string, replacementKey: string) => {
        messageTemp = messageTemp.replace(`{${replacementKey}}`, value);
        this.title.replace(`{${replacementKey}}`, value);
      });
    }
    this.message = messageTemp;
  }
}
