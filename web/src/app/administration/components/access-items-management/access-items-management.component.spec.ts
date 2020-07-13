import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { configureTests } from 'app/app.test.configuration';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { AccessIdDefinition } from 'app/shared/models/access-id';
import { AccessItemWorkbasketResource } from 'app/shared/models/access-item-workbasket-resource';
import { of } from 'rxjs';
import { NgxsModule } from '@ngxs/store';
import { AccessItemsManagementComponent } from './access-items-management.component';

describe('AccessItemsManagementComponent', () => {
  let component: AccessItemsManagementComponent;
  let fixture: ComponentFixture<AccessItemsManagementComponent>;
  let accessIdsService;

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [NgxsModule.forRoot()],
      declarations: [AccessItemsManagementComponent],
      providers: [AccessIdsService, FormsValidatorService]
    });
  };

  beforeEach(done => {
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(AccessItemsManagementComponent);
      component = fixture.componentInstance;
      accessIdsService = testBed.get(AccessIdsService);
      spyOn(accessIdsService, 'getAccessItems').and.returnValue(of(new Array<AccessIdDefinition>()));
      spyOn(accessIdsService, 'searchForAccessId').and.returnValue(of(new AccessItemWorkbasketResource()));
      spyOn(accessIdsService, 'getGroupsByAccessId').and.returnValue(of(new AccessItemWorkbasketResource()));
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
