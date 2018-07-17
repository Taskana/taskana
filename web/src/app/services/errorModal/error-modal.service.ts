import { Injectable } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';
import { ErrorModel } from 'app/models/modal-error';

@Injectable()
export class ErrorModalService {

	private errorTriggered = new Subject<ErrorModel>();

	constructor() { }

	triggerError(error: ErrorModel) {
		this.errorTriggered.next(error);
	}

	getError(): Observable<ErrorModel> {
		return this.errorTriggered.asObservable();
	}
}
