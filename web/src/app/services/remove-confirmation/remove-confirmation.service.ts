import { Injectable } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';

@Injectable()
export class RemoveConfirmationService {

	private removeConfirmationCallbackSubject = new Subject<{ callback: Function, message: string }>();
	private removeConfirmationCallback: Function;

	constructor() { }

	setRemoveConfirmation(callback: Function, message: string) {
		this.removeConfirmationCallback = callback;
		this.removeConfirmationCallbackSubject.next({ callback: callback, message: message });
	}

	getRemoveConfirmation(): Observable<{ callback: Function, message: string }> {
		return this.removeConfirmationCallbackSubject.asObservable();
	}

	runCallbackFunction() {
		this.removeConfirmationCallback();
	}
}
