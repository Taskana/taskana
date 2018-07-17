import { Injectable } from '@angular/core';
import { Observable ,  Subject } from 'rxjs';

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
