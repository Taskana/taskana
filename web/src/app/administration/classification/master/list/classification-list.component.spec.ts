import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { TreeNode } from 'app/models/tree-node';

import { ClassificationListComponent } from './classification-list.component';
import { ImportExportComponent } from 'app/shared/import-export/import-export.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { ClassificationTypesSelectorComponent } from 'app/shared/classification-types-selector/classification-types-selector.component';
import { MapValuesPipe } from 'app/pipes/mapValues/map-values.pipe';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { WorkbasketDefinitionService } from 'app/services/workbasket-definition/workbasket-definition.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ClassificationsService } from 'app/services/classifications/classifications.service';
import { ClassificationDefinitionService } from 'app/services/classification-definition/classification-definition.service';
import { DomainService } from 'app/services/domains/domain.service';

@Component({
  selector: 'taskana-tree',
  template: ''
})
class TreeComponent {
  @Input() treeNodes;
}

describe('ClassificationListComponent', () => {
  let component: ClassificationListComponent;
  let fixture: ComponentFixture<ClassificationListComponent>;
  const treeNodes: Array<TreeNode> = new Array(new TreeNode());
  const classificationTypes: Map<string, string> = new Map<string, string>([['type1', 'type1'], ['type2', 'type2']])
  let classificationsSpy, classificationsTypesSpy;
  let classificationsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationListComponent, ImportExportComponent, SpinnerComponent, ClassificationTypesSelectorComponent,
        TreeComponent, MapValuesPipe],
      imports: [HttpClientModule],
      providers: [
        HttpClient, WorkbasketDefinitionService, AlertService, ClassificationsService, DomainService, ClassificationDefinitionService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationListComponent);
    component = fixture.componentInstance;
    classificationsService = TestBed.get(ClassificationsService);
    classificationsSpy = spyOn(classificationsService, 'getClassifications').and.returnValue(Observable.of(treeNodes));
    classificationsTypesSpy = spyOn(classificationsService, 'getClassificationTypes').and.returnValue(Observable.of(classificationTypes));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
