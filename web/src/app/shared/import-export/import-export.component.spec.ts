import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportExportComponent} from './import-export.component';
import {WorkbasketService} from '../../services/workbasket/workbasket.service';
import {ClassificationDefinitionService} from '../../services/classification-definition/classification-definition.service';
import {WorkbasketDefinitionService} from '../../services/workbasket-definition/workbasket-definition.service';
import {AlertService} from '../../services/alert/alert.service';
import {HttpClientModule} from '@angular/common/http';
import {DomainService} from '../../services/domains/domain.service';

describe('ImportExportComponent', () => {
  let component: ImportExportComponent;
  let fixture: ComponentFixture<ImportExportComponent>;
  let domainService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ImportExportComponent],
      imports: [HttpClientModule],
      providers: [WorkbasketService, ClassificationDefinitionService, WorkbasketDefinitionService, AlertService, DomainService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportExportComponent);
    component = fixture.componentInstance;
    domainService = TestBed.get(DomainService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
