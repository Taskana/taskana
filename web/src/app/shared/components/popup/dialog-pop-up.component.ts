import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { notifications } from '../../models/notifications';

@Component({
  selector: 'taskana-shared-dialog-pop-up',
  templateUrl: './dialog-pop-up.component.html',
  styleUrls: ['./dialog-pop-up.component.scss']
})
export class DialogPopUpComponent implements OnInit {
  title: string;
  message: string;
  isDialog: false;
  callback: Function;

  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {}

  ngOnInit() {
    if (this.data) {
      this.isDialog = this.data.isDialog;
      if (this.isDialog) {
        this.initDialog();
      } else {
        this.initError();
      }
    } else {
      this.message = 'There was an error with this PopUp. \nPlease contact your administrator.';
    }
  }

  initError() {
    this.title = notifications.get(this.data.key).left || '';
    this.message =
      notifications.get(this.data.key).right || (this.data && this.data.passedError && this.data.passedError.error)
        ? this.data.passedError.error.message
        : '';
    if (this.data.additions) {
      this.data.additions.forEach((value: string, replacementKey: string) => {
        this.message = this.message.replace(`{${replacementKey}}`, value);
        this.title = this.title.replace(`{${replacementKey}}`, value);
      });
    }
  }

  initDialog() {
    this.message = this.data.message;
    this.title = 'Please confirm your action';
    this.callback = this.data.callback;
  }
}
