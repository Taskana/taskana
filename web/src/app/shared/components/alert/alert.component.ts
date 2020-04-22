import { Component, OnInit } from '@angular/core';

import { NotificationService } from 'app/shared/services/notifications/notification.service';
import { AlertModel } from '../../models/alert-model';
import { expandTop } from '../../../../theme/animations/expand.animation';

@Component({
  selector: 'taskana-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss'],
  animations: [expandTop]
})

export class AlertComponent implements OnInit {
  alert: AlertModel;
  private timeoutId: any; // NodeJS.Timer cannot be imported..
  constructor(private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.notificationService.getAlert().subscribe((alert: AlertModel) => {
      this.alert = alert;
      if (alert.autoClosing) {
        this.setTimeOutForClosing(alert.closingDelay);
      }
    });
  }

  setTimeOutForClosing(time: number) {
    clearTimeout(this.timeoutId);
    this.timeoutId = setTimeout(() => {
      delete this.alert;
    }, time);
  }
}
