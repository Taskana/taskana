import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { configureTests } from 'app/app.test.configuration';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { AccessIdDefinition } from 'app/models/access-id';
import { AccessItemsWorkbasketResource } from 'app/models/access-item-workbasket-resource';
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
      spyOn(accessIdsService, 'getAccessItemsPermissions').and.returnValue(of(new Array<AccessIdDefinition>()));
      spyOn(accessIdsService, 'getAccessItemsInformation').and.returnValue(of(new AccessItemsWorkbasketResource()));
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
