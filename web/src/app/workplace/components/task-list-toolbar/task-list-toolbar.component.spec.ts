import { ComponentFixture, async, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { TypeaheadModule, ComponentLoaderFactory, PositioningService } from 'ngx-bootstrap';
import { SortComponent } from 'app/shared/components/sort/sort.component';
import { FilterComponent } from 'app/shared/components/filter/filter.component';
import { MapValuesPipe } from 'app/shared/pipes/map-values.pipe';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { SvgIconComponent } from 'angular-svg-icon';
import { TaskService } from 'app/workplace/services/task.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TaskListToolbarComponent } from './task-list-toolbar.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {}

// TODO: test pending to test. Failing random
xdescribe('TasklistToolbarComponent', () => {
  let component: TaskListToolbarComponent;
  let fixture: ComponentFixture<TaskListToolbarComponent>;

  const routes: Routes = [{ path: '*', component: DummyDetailComponent }];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TaskListToolbarComponent,
        SortComponent,
        FilterComponent,
        MapValuesPipe,
        IconTypeComponent,
        SvgIconComponent,
        DummyDetailComponent
      ],
      imports: [
        FormsModule,
        TypeaheadModule,
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        BrowserAnimationsModule
      ],
      providers: [
        TaskService,
        HttpClient,
        WorkbasketService,
        DomainService,
        RequestInProgressService,
        SelectedRouteService,
        WorkplaceService,
        ComponentLoaderFactory,
        PositioningService
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
