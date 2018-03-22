import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';



@Injectable()
export class RequestInProgressService {

	public requestInProgressTriggered = new Subject<boolean>();

	constructor() { }

	setRequestInProgress(value: boolean) {
		this.requestInProgressTriggered.next(value);
	}

	getRequestInProgress(): Observable<boolean> {
		return this.requestInProgressTriggered.asObservable();
	}
}
