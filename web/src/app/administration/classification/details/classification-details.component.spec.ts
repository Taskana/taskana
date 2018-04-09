import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { ClassificationDetailsComponent } from './classification-details.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';

import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { ClassificationsService } from 'app/services/classifications/classifications.service';
import { TreeNodeModel } from 'app/models/tree-node';


@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

const routes: Routes = [
  { path: ':id', component: DummyDetailComponent, outlet: 'detail' },
  { path: 'classifications', component: DummyDetailComponent }
];

describe('ClassificationDetailsComponent', () => {
  let component: ClassificationDetailsComponent;
  let fixture: ComponentFixture<ClassificationDetailsComponent>;
  const treeNodes: Array<TreeNodeModel> = new Array(new TreeNodeModel());
  const classificationTypes: Array<string> = new Array<string>('type1', 'type2');
  let classificationsSpy, classificationsTypesSpy;
  let classificationsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, RouterTestingModule.withRoutes(routes)],
      declarations: [ClassificationDetailsComponent, SpinnerComponent, DummyDetailComponent],
      providers: [MasterAndDetailService, RequestInProgressService, ClassificationsService, HttpClient]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationDetailsComponent);
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
