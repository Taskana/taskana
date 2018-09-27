import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs/index';
import {Workbasket} from 'app/models/workbasket';

@Injectable()
export class WorkplaceService {
  currentWorkbasket: Workbasket = undefined;
  workbasketSelectedSource = new Subject<Workbasket>();
  workbasketSelectedStream = this.workbasketSelectedSource.asObservable();

  selectWorkbasket(workbasket: Workbasket) {
    this.currentWorkbasket = workbasket;
    this.workbasketSelectedSource.next(workbasket);
  }

  getSelectedWorkbasket(): Observable<Workbasket> {
    return this.workbasketSelectedStream;
  }
}
