import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { MessageModal } from 'app/models/message-modal';

@Injectable()
export class GeneralModalService {
  private messageTriggered = new Subject<MessageModal>();

  triggerMessage(message: MessageModal) {
    this.messageTriggered.next(message);
  }

  getMessage(): Observable<MessageModal> {
    return this.messageTriggered.asObservable();
  }
}
