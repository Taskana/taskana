import { Injectable } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';

export class SavingInformation {
    constructor(public url: string,
        public workbasketId: string) {
    }
}


@Injectable()
export class SavingWorkbasketService {

    public distributionTargetsSavingInformation = new Subject<SavingInformation>();
    public accessItemsSavingInformation = new Subject<SavingInformation>();

    constructor() { }

    triggerDistributionTargetSaving(distributionTargetInformation: SavingInformation) {
        this.distributionTargetsSavingInformation.next(distributionTargetInformation);
    }
    triggerAccessItemsSaving(accessItemsInformation: SavingInformation) {
        this.accessItemsSavingInformation.next(accessItemsInformation);
    }
    triggeredDistributionTargetsSaving(): Observable<SavingInformation> {
        return this.distributionTargetsSavingInformation.asObservable();
    }
    triggeredAccessItemsSaving(): Observable<SavingInformation> {
        return this.accessItemsSavingInformation.asObservable();
    }
}
