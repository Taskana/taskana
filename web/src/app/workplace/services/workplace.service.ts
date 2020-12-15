import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Workbasket } from 'app/shared/models/workbasket';
import { ObjectReference } from '../models/object-reference';

@Injectable()
export class WorkplaceService {
  private workbasketSelected = new BehaviorSubject<Workbasket>(undefined);
  private objectReferenceSelected = new BehaviorSubject<ObjectReference>(undefined);

  selectWorkbasket(workbasket?: Workbasket): void {
    this.workbasketSelected.next(workbasket);
  }

  getSelectedWorkbasket(): Observable<Workbasket> {
    return this.workbasketSelected.asObservable();
  }

  selectObjectReference(objectReference?: ObjectReference): void {
    this.objectReferenceSelected.next(objectReference);
  }

  getSelectedObjectReference(): Observable<ObjectReference> {
    return this.objectReferenceSelected.asObservable();
  }
}
