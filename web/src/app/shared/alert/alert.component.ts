import { Component, OnInit } from '@angular/core';

import { AlertModel } from 'app/models/alert';
import { AlertService } from 'app/services/alert/alert.service';
import { expandTop } from '../animations/expand.animation';

@Component({
	selector: 'taskana-alert',
	templateUrl: './alert.component.html',
	styleUrls: ['./alert.component.scss'],
	animations: [expandTop]
})

export class AlertComponent implements OnInit {
	alert: AlertModel;
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
		setTimeout(() => {
			this.alert = undefined;
		}, time);
	}
}
