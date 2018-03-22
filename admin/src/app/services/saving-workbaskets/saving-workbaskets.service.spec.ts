import { TestBed, inject } from '@angular/core/testing';

import { SavingWorkbasketService } from './saving-workbaskets.service';

describe('SavingWorkbasketsService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [SavingWorkbasketService]
        });
    });

    it('should be created', inject([SavingWorkbasketService], (service: SavingWorkbasketService) => {
        expect(service).toBeTruthy();
    }));
});
