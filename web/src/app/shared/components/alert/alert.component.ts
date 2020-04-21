import { Component, OnInit } from '@angular/core';

import { AlertModel } from 'app/shared/models/alert';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { expandTop } from '../../animations/expand.animation';

@Component({
  selector: 'taskana-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss'],
  animations: [expandTop]
})

export class AlertComponent implements OnInit {
  alert: AlertModel;
  private timeoutId: any; // NodeJS.Timer cannot be imported..
  constructor(private alertService: AlertService) { }

  ngOnInit() {
    this.alertService.getAlert().subscribe((alert: AlertModel) => {
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
