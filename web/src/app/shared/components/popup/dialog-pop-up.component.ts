import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ObtainMessageService } from '../../services/obtain-message/obtain-message.service';
import { messageTypes } from '../../services/obtain-message/message-types';

@Component({
  selector: 'taskana-shared-dialog-pop-up',
  templateUrl: './dialog-pop-up.component.html',
  styleUrls: ['./dialog-pop-up.component.scss']
})
export class DialogPopUpComponent implements OnInit {
  message: string;
  callback: Function;
  isDataSpecified: boolean;

  constructor(@Inject(MAT_DIALOG_DATA) private data: any, private obtainMessageService: ObtainMessageService) {}

  ngOnInit() {
    this.isDataSpecified = this.data?.message && this.data?.callback;
    if (this.isDataSpecified) {
      this.message = this.data.message;
      this.callback = this.data.callback;
    } else {
      this.message = this.obtainMessageService.getMessage('POPUP_CONFIGURATION', {}, messageTypes.DIALOG);
    }
  }
}
