import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportExportComponent } from './import-export.component';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from '../../services/workbasket-definition/workbasket-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { HttpClientModule } from '@angular/common/http';
import { DomainService } from 'app/services/domain/domain.service';
import { of } from 'rxjs';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';

describe('ImportExportComponent', () => {
  let component: ImportExportComponent;
  let fixture: ComponentFixture<ImportExportComponent>;
  let domainService;
  let debugElement;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [ImportExportComponent],
        imports: [HttpClientModule, AngularSvgIconModule],
        providers: [WorkbasketService, ClassificationDefinitionService, WorkbasketDefinitionService, AlertService,
          ErrorModalService]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(ImportExportComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      fixture.detectChanges();
      done();
    });
  });

  afterEach(() => {
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update domains', () => {
    domainService = TestBed.get(DomainService);
    spyOn(domainService, 'getDomains').and.returnValue(of(['A', 'B']));
    component.updateDomains();
    expect(domainService.getDomains).toHaveBeenCalled();
  });
});
