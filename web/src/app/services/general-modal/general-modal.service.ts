import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { MessageModal } from 'app/models/message-modal';

import { ERROR_TYPES, errors as ERRORS } from './errors';

@Injectable()
export class GeneralModalService {
  private messageTriggered = new Subject<MessageModal>();

  triggerMessage(message: MessageModal) {
	console.log(ERRORS.get(ERROR_TYPES.DELETE_ERR));
    this.messageTriggered.next(message);
  }

  getMessage(): Observable<MessageModal> {
    return this.messageTriggered.asObservable();
  }
}
