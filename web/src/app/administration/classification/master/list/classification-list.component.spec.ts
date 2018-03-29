import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClassificationListComponent} from './classification-list.component';
import {ImportExportComponent} from '../../../import-export/import-export.component';
import {SpinnerComponent} from '../../../../shared/spinner/spinner.component';
import {WorkbasketService} from '../../../../services/workbasket/workbasket.service';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {WorkbasketDefinitionService} from '../../../../services/workbasket/workbasketDefinition.service';
import {AlertService} from '../../../../services/alert/alert.service';
import {ClassificationService} from '../../../../services/classification/classification.service';
import {DomainService} from '../../../../services/domains/domain.service';

describe('ClassificationListComponent', () => {
  let component: ClassificationListComponent;
  let fixture: ComponentFixture<ClassificationListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationListComponent, ImportExportComponent, SpinnerComponent],
      imports: [HttpClientModule],
      providers: [
        WorkbasketService, HttpClient, WorkbasketDefinitionService, AlertService, ClassificationService, DomainService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
