import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ErrorModel } from '../../models/error-model';
import { NOTIFICATION_TYPES } from '../../models/notifications';
import { ToastComponent } from '../../components/toast/toast.component';
import { DialogPopUpComponent } from '../../components/popup/dialog-pop-up.component';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private matSnack: MatSnackBar, private popup: MatDialog) {
  }

  triggerError(key: NOTIFICATION_TYPES, passedError?: HttpErrorResponse, additions?: Map<String, String>): void {
    this.popup.open(DialogPopUpComponent, {
      data: { key, passedError, additions, isDialog: false },
      backdropClass: 'backdrop',
      position: { top: '3em' },
      autoFocus: true,
      maxWidth: '50em',
    });
  }

  showDialog(message: string, callback?: Function): MatDialogRef<DialogPopUpComponent> {
    const ref = this.popup.open(DialogPopUpComponent, {
      data: { isDialog: true, message, callback },
      backdropClass: 'backdrop',
      position: { top: '3em' },
      autoFocus: true,
      maxWidth: '50em',
    });
    ref.beforeClosed().subscribe(call => {
      if (typeof call === 'function') {
        call();
      }
    });
    return ref;
  }

  showToast(key: NOTIFICATION_TYPES, additions?: Map<string, string>) {
    let colorClass: string[];
    const type = NOTIFICATION_TYPES[key].split('_')[0].toLowerCase();
    switch (type) {
      case 'danger': colorClass = ['red', 'background-white'];
        break;
      case 'success': colorClass = ['white', 'background-bluegreen'];
        break;
      case 'info': colorClass = ['white', 'background-darkgreen'];
        break;
      case 'warning': colorClass = ['brown', 'background-white'];
        break;
      default: colorClass = ['white', 'background-darkgreen'];
    }
    return this.matSnack.openFromComponent(ToastComponent, {
      duration: 5000,
      data: { key, additions },
      panelClass: colorClass
    });
  }
}
