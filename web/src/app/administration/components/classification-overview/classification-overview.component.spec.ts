import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Location } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxsModule } from '@ngxs/store';
import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router, Routes } from '@angular/router';
import { ClassificationOverviewComponent } from './classification-overview.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {}

describe('ClassificationOverviewComponent', () => {
  let component: ClassificationOverviewComponent;
  let fixture: ComponentFixture<ClassificationOverviewComponent>;
  let router;
  let debugElement;
  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go']);

  beforeEach(() => {
    const routes: Routes = [{ path: ':id', component: DummyDetailComponent }];

    TestBed.configureTestingModule({
      declarations: [ClassificationOverviewComponent, DummyDetailComponent],
      imports: [RouterTestingModule.withRoutes(routes), NgxsModule.forRoot()],
      providers: [{ provide: Location, useValue: locationSpy }],
      schemas: [NO_ERRORS_SCHEMA]
    });

    fixture = TestBed.createComponent(ClassificationOverviewComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    router = TestBed.get(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
