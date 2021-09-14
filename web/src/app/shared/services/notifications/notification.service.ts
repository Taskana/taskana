import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogPopUpComponent } from '../../components/popup/dialog-pop-up.component';
import { HotToastService } from '@ngneat/hot-toast';
import { ObtainMessageService } from '../obtain-message/obtain-message.service';
import { messageTypes } from '../obtain-message/message-types';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(
    private popup: MatDialog,
    private toastService: HotToastService,
    private obtainMessageService: ObtainMessageService
  ) {}

  generateToastId(errorKey: string, messageVariables: object): string {
    let id = errorKey;
    for (const [replacementKey, value] of Object.entries(messageVariables)) {
      id = id.concat(replacementKey, value);
    }
    return id;
  }

  showError(errorKey: string, messageVariables: object = {}) {
    this.toastService.error(this.obtainMessageService.getMessage(errorKey, messageVariables, messageTypes.ERROR), {
      dismissible: true,
      autoClose: false,
      id: this.generateToastId(errorKey, messageVariables)
    });
  }

  showSuccess(successKey: string, messageVariables: object = {}) {
    this.toastService.success(
      this.obtainMessageService.getMessage(successKey, messageVariables, messageTypes.SUCCESS),
      {
        duration: 5000
      }
    );
  }

  showInformation(informationKey: string, messageVariables: object = {}) {
    this.toastService.show(
      `
    <span class="material-icons">info</span> ${this.obtainMessageService.getMessage(
      informationKey,
      messageVariables,
      messageTypes.INFORMATION
    )}
  `,
      // prevents duplicated toast because of double call in task-master
      // TODO: delete while frontend refactoring
      { id: 'empty-workbasket' }
    );
  }

  showWarning(warningKey: string, messageVariables: object = {}) {
    this.toastService.warning(this.obtainMessageService.getMessage(warningKey, messageVariables, messageTypes.WARNING));
  }

  showDialog(key: string, messageVariables: object = {}, callback: Function) {
    const message = this.obtainMessageService.getMessage(key, messageVariables, messageTypes.DIALOG);

    const ref = this.popup.open(DialogPopUpComponent, {
      data: { message: message, callback },
      backdropClass: 'backdrop',
      position: { top: '5em' },
      autoFocus: true,
      maxWidth: '50em'
    });
    ref.beforeClosed().subscribe((call) => {
      if (typeof call === 'function') {
        call();
      }
    });
    return ref;
  }
}
