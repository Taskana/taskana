import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable()
export class RequestInProgressService {

  public requestInProgressTriggered = new Subject<boolean>();

  constructor() { }

  setRequestInProgress(value: boolean) {
    setTimeout(() => this.requestInProgressTriggered.next(value), 0);
  }

  getRequestInProgress(): Observable<boolean> {
    return this.requestInProgressTriggered.asObservable();
  }
}
