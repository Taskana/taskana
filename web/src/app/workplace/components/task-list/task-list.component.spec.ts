import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { Component, ChangeDetectorRef } from '@angular/core';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { TaskListComponent } from './task-list.component';
import { DateTimeZonePipe } from '../../../shared/pipes/date-time-zone.pipe';
import { MatSelectModule } from '@angular/material/select';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';

@Component({
  selector: 'kadai-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {}

@Component({
  selector: 'svg-icon',
  template: '<p>Mock Icon Component</p>'
})
export class MockSvgIconComponent {}

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;

  const routes: Routes = [{ path: '*', component: DummyDetailComponent }];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        RouterTestingModule.withRoutes(routes),
        HttpClientModule,
        MatSelectModule,
        MatListModule,
        MatBadgeModule
      ],
      declarations: [TaskListComponent, DummyDetailComponent, MockSvgIconComponent, DateTimeZonePipe],
      providers: [WorkplaceService, ChangeDetectorRef]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
