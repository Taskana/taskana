import { Component, OnInit } from '@angular/core';
import { trigger, state, style, animate, transition } from '@angular/animations';

import { AlertModel } from 'app/models/alert';
import { AlertService } from 'app/services/alert/alert.service';

@Component({
	selector: 'taskana-alert',
	templateUrl: './alert.component.html',
	styleUrls: ['./alert.component.scss'],
	animations: [
		trigger('alertState', [
			state('in', style({ transform: 'translateY(0)', overflow: 'hidden' })),
			transition('void => *', [
				style({ transform: 'translateY(100%)', overflow: 'hidden' }),
				animate(100)
			]),
			transition('* => void', [
				animate(100, style({ transform: 'translateY(100%)', overflow: 'hidden' }))
			])
		])
	]
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
