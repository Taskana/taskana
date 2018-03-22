import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { ErrorModel } from '../model/modal-error';



@Injectable()
export class ErrorModalService {

	public errorTriggered = new Subject<ErrorModel>();

	constructor() { }

	triggerError(error: ErrorModel) {
		this.errorTriggered.next(error);
	}

	getError(): Observable<ErrorModel> {
		return this.errorTriggered.asObservable();
	}
}
