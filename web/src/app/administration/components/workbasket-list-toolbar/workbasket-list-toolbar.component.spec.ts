import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Router, Routes } from '@angular/router';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedModule } from 'app/shared/shared.module';
import { AppModule } from 'app/app.module';

import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Links } from 'app/shared/models/links';
import { Filter } from 'app/shared/models/filter';
import { Sorting } from 'app/shared/models/sorting';

import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { configureTests } from 'app/app.test.configuration';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {

}

describe('WorkbasketListToolbarComponent', () => {
  let component: WorkbasketListToolbarComponent;
  let fixture: ComponentFixture<WorkbasketListToolbarComponent>;
  let debugElement;
  let workbasketService;
  let router;

  const routes: Routes = [
    { path: ':id', component: DummyDetailComponent, outlet: 'detail' }
  ];

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [FormsModule, ReactiveFormsModule, AngularSvgIconModule,
          HttpClientModule, RouterTestingModule.withRoutes(routes), SharedModule, AppModule],
        declarations: [WorkbasketListToolbarComponent, DummyDetailComponent, ImportExportComponent],
        providers: [
          WorkbasketService,
          ClassificationDefinitionService,
          WorkbasketDefinitionService,
          ImportExportService
        ]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(WorkbasketListToolbarComponent);
      workbasketService = testBed.get(WorkbasketService);
      router = testBed.get(Router);
      spyOn(workbasketService, 'markWorkbasketForDeletion').and.returnValue(of(''));
      spyOn(workbasketService, 'triggerWorkBasketSaved');

      debugElement = fixture.debugElement.nativeElement;
      component = fixture.componentInstance;
      component.workbaskets = [
        new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1')
      ];
      component.workbaskets[0].markedForDeletion = false;

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

  it('should navigate to new-workbasket when click on add new workbasket', () => {
    const spy = spyOn(router, 'navigate');
    component.addWorkbasket();
    expect(spy.calls.first().args[0][0].outlets.detail[0]).toBe('new-workbasket');
  });

  it('should emit performSorting when sorting is triggered', () => {
    let sort: Sorting;
    const compareSort = new Sorting();

    component.performSorting.subscribe(value => { sort = value; });
    component.sorting(compareSort);
    expect(sort).toBe(compareSort);
  });

  it('should emit performFilter when filter is triggered', () => {
    let filter: Filter;
    const compareFilter = new Filter();

    component.performFilter.subscribe(value => { filter = value; });
    component.filtering(compareFilter);
    expect(filter).toBe(compareFilter);
  });
});
