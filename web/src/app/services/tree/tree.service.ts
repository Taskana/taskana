import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class TreeService {
  public removedNodeId = new Subject<string>();

  setRemovedNodeId(value: string) {
    this.removedNodeId.next(value);
  }

  getRemovedNodeId() {
    return this.removedNodeId.asObservable();
  }
}
