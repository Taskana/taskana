import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportExportComponent} from './import-export.component';
import {WorkbasketService} from '../../services/workbasket/workbasket.service';
import {ClassificationDefinitionService} from 'app/administration/services/classification-definition/classification-definition.service';
import {WorkbasketDefinitionService} from '../../services/workbasket-definition/workbasket-definition.service';
import {AlertService} from 'app/services/alert/alert.service';
import {HttpClientModule} from '@angular/common/http';
import {DomainService} from 'app/services/domain/domain.service';
import {Observable} from 'rxjs/Observable';
import {ErrorModalService} from 'app/services/errorModal/error-modal.service';
import { DomainServiceMock } from 'app/services/domain/domain.service.mock';

describe('ImportExportComponent', () => {
  let component: ImportExportComponent;
  let fixture: ComponentFixture<ImportExportComponent>;
  let domainService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ImportExportComponent],
      imports: [HttpClientModule],
      providers: [WorkbasketService, ClassificationDefinitionService, WorkbasketDefinitionService, AlertService,  {
        provide: DomainService,
        useClass: DomainServiceMock
      },
        ErrorModalService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportExportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update domains', () => {
    domainService = TestBed.get(DomainService);
    spyOn(domainService, 'getDomains').and.returnValue(Observable.of(['A', 'B']));
    component.updateDomains();
    expect(domainService.getDomains).toHaveBeenCalled();
  });
});
