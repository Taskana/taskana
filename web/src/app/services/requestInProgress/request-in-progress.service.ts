import { Injectable } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';



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
