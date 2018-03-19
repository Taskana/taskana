import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';


export enum AlertType {
	SUCCESS = 'success',
	INFO = 'info',
	WARNING = 'warning',
	DANGER = 'danger',
}

export class AlertModel {

	constructor(public type: string = AlertType.SUCCESS,
		public text: string = 'Success',
		public autoClosing: boolean = true,
		public closingDelay: number = 2500) {
	}
}

@Injectable()
export class AlertService {

	public alertTriggered = new Subject<AlertModel>();

	constructor() { }

	triggerAlert(alert: AlertModel) {
		this.alertTriggered.next(alert);
	}

	getAlert(): Observable<AlertModel> {
		return this.alertTriggered.asObservable();
	}
}
