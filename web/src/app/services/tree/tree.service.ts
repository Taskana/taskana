import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class TreeService {

    public removedNodeId = new Subject<string>();

    constructor() { }

    setRemovedNodeId(value: string) {
        this.removedNodeId.next(value);
    }

    getRemovedNodeId() {
        return this.removedNodeId.asObservable();
    }

}
