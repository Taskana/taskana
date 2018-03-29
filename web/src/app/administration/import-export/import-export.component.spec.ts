import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportExportComponent} from './import-export.component';
import {WorkbasketService} from '../../services/workbasket/workbasket.service';
import {ClassificationService} from '../../services/classification/classification.service';
import {WorkbasketDefinitionService} from '../../services/workbasket/workbasketDefinition.service';
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
      providers: [WorkbasketService, ClassificationService, WorkbasketDefinitionService, AlertService, DomainService]
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

  // TODO add tests for inport/export
  /*
  it('should update domains', () => {
    let domains = [];
    domainService.getDomains().subscribe(
      result => domains = result
    );
    component.updateDomains();
    expect(component.domains).toEqual(domains);
  })
  */
});
