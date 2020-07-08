import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { NgxsModule } from '@ngxs/store';
import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { Location } from '@angular/common';
import { take } from 'rxjs/operators';
import { debug } from 'util';
import { WorkbasketOverviewComponent } from './workbasket-overview.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {
}
@Component({
  selector: 'taskana-workbasket-list',
  template: 'dummylist'
})
export class DummyListComponent {
}
describe('WorkbasketOverviewComponent', () => {
  let debugElement: any;
  let component: WorkbasketOverviewComponent;
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;
  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go']);
  const routes: Routes = [
    { path: ':id', component: DummyDetailComponent }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule(
      { imports: [
        RouterTestingModule.withRoutes(routes),
        NgxsModule.forRoot()
      ],
      declarations: [
        WorkbasketOverviewComponent,
        DummyDetailComponent,
        DummyListComponent],
      providers: [
        { provide: Location, useValue: locationSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA] }
    )
      .compileComponents();
  }));

  afterEach(() => {
    fixture.destroy();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not display detail component if showDetail is false', () => {
    component.showDetail = false;
    expect(debugElement.querySelector('taskana-workbasket-details')).toBeNull();
  });

  it('should display detail component if showDetail is true', () => {
    component.showDetail = true;
    fixture.detectChanges();
    expect(debugElement.querySelector('taskana-workbasket-details')).toBeTruthy();
  });
});
