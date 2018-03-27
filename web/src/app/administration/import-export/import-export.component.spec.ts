import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportExportComponent } from './import-export.component';
import {WorkbasketService} from '../../services/workbasket/workbasket.service';
import {ClassificationService} from '../../services/classification/classification.service';
import {WorkbasketDefinitionService} from '../../services/workbasket/workbasketDefinition.service';
import {AlertService} from '../../services/alert/alert.service';
import {HttpClient, HttpClientModule} from '@angular/common/http';

describe('ImportExportComponent', () => {
  let component: ImportExportComponent;
  let fixture: ComponentFixture<ImportExportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImportExportComponent],
      imports: [HttpClientModule],
      providers: [WorkbasketService, ClassificationService, WorkbasketDefinitionService, AlertService]
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
});
