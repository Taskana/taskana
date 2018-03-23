import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { AlertModel } from 'app/models/alert';

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
