import { Component, Inject, Input, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { NOTIFICATION_TYPES, notifications } from '../../models/notifications';

@Component({
  selector: 'taskana-shared-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss']
})
export class ToastComponent implements OnInit {
  message: string;
  type: string = 'info';

  constructor(@Inject(MAT_SNACK_BAR_DATA) private data: any) {}

  ngOnInit(): void {
    if (this.data) {
      this.message = notifications.get(this.data.key).right;
      if (this.data.additions) {
        this.data.additions.forEach((value: string, replacementKey: string) => {
          this.message = this.message.replace(`{${replacementKey}}`, value);
        });
      }
      this.type = NOTIFICATION_TYPES[this.data.key].split('_')[0].toLowerCase();
      if (this.type === 'danger') {
        this.type = 'error';
      }
      if (this.type === 'success') {
        this.type = 'done';
      }
    } else {
      this.message = 'There was an error with this toast. \nPlease contact your administrator.';
    }
  }
}
