import { Injectable } from '@angular/core';
import { messageByErrorCode } from './message-by-error-code';
import { messageTypes } from './message-types';

@Injectable({
  providedIn: 'root'
})
export class ObtainMessageService {
  getMessage(key: string, messageVariables: object = {}, type: messageTypes): string {
    let message =
      messageByErrorCode[type][key] ||
      messageByErrorCode[type]['FALLBACK'] ||
      `The message with type '${type}' and key '${key}' is not configured`;

    for (const [replacementKey, value] of Object.entries(messageVariables)) {
      message = message.replace(`{${replacementKey}}`, `'${value}'`);
    }

    return message;
  }
}
