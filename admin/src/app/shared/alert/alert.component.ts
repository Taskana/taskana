import { Component, OnInit } from '@angular/core';
import { AlertService, AlertModel } from '../../services/alert.service';
import { trigger, state, style, animate, transition } from '@angular/animations';

@Component({
	selector: 'taskana-alert',
	templateUrl: './alert.component.html',
	styleUrls: ['./alert.component.scss'],
	animations: [
		trigger('alertState', [
			state('in', style({ transform: 'translateY(0)' })),
			transition('void => *', [
				style({ transform: 'translateY(100%)' }),
				animate(100)
			]),
			transition('* => void', [
				animate(100, style({ transform: 'translateY(100%)' }))
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
