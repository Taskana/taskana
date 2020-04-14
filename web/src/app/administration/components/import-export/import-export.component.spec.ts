import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { HttpClientModule } from '@angular/common/http';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { configureTests } from 'app/app.test.configuration';
import { ImportExportService } from 'app/administration/services/import-export/import-export.service';
import { WorkbasketDefinitionService } from '../../services/workbasket-definition.service';
import { ImportExportComponent } from './import-export.component';

describe('ImportExportComponent', () => {
  let component: ImportExportComponent;
  let fixture: ComponentFixture<ImportExportComponent>;
  let debugElement;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [ImportExportComponent],
        imports: [HttpClientModule, AngularSvgIconModule],
        providers: [WorkbasketService, ClassificationDefinitionService, WorkbasketDefinitionService, AlertService,
          GeneralModalService, ImportExportService]
      });
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
});
