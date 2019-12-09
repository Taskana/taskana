import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Workbasket } from 'app/models/workbasket';
import { ObjectReference } from '../models/object-reference';

@Injectable()
export class WorkplaceService {
  // necessary because the TaskdetailsComponent is not always initialized when the first workbasket was selected.
  currentWorkbasket: Workbasket;
  objectReference: ObjectReference
  private workbasketSelectedSource = new Subject<Workbasket>();
  workbasketSelectedStream = this.workbasketSelectedSource.asObservable();
  private objectReferenceSource = new Subject<ObjectReference>();
  objectReferenceSelectedStream = this.objectReferenceSource.asObservable();

  selectWorkbasket(workbasket?: Workbasket) {
    this.currentWorkbasket = workbasket;
    this.workbasketSelectedSource.next(workbasket);
  }

  getSelectedWorkbasket(): Observable<Workbasket> {
    return this.workbasketSelectedStream;
  }

  selectObjectReference(objectReference?: ObjectReference) {
    this.objectReference = new ObjectReference();
    if (objectReference) {
      this.objectReference.type = objectReference.type;
      this.objectReference.value = objectReference.value;
    }
    this.objectReferenceSource.next(objectReference);
  }

  getObjectReference() {
    return this.objectReferenceSelectedStream;
  }
}
