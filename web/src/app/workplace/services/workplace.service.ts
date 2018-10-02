import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Workbasket } from 'app/models/workbasket';

@Injectable()
export class WorkplaceService {
  // necessary because the TaskdetailsComponent is not always initialized when the first workbasket was selected.
  currentWorkbasket: Workbasket;
  private workbasketSelectedSource = new Subject<Workbasket>();
  workbasketSelectedStream = this.workbasketSelectedSource.asObservable();

  selectWorkbasket(workbasket: Workbasket) {
    this.currentWorkbasket = workbasket;
    this.workbasketSelectedSource.next(workbasket);
  }

  getSelectedWorkbasket(): Observable<Workbasket> {
    return this.workbasketSelectedStream;
  }
}
