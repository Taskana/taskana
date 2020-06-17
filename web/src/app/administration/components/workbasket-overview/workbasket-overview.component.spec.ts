import { async, ComponentFixture, TestBed } from '@angular/core/testing';

<<<<<<< HEAD
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Routes } from '@angular/router';
import { WorkbasketOverviewComponent } from './workbasket-overview.component';
import { SharedModule } from '../../../shared/shared.module';
import { AppModule } from '../../../app.module';
import { ImportExportComponent } from '../import-export/import-export.component';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { WorkbasketDefinitionService } from '../../services/workbasket-definition.service';
import { ImportExportService } from '../../services/import-export.service';
import { DummyDetailComponent } from '../workbasket-list-toolbar/workbasket-list-toolbar.component.spec';
import { OrientationService } from '../../../shared/services/orientation/orientation.service';
=======
import { WorkbasketOverviewComponent } from './workbasket-overview.component';
>>>>>>> TSK-1215 initialized workbasket-overview

describe('WorkbasketOverviewComponent', () => {
  let component: WorkbasketOverviewComponent;
  let fixture: ComponentFixture<WorkbasketOverviewComponent>;

<<<<<<< HEAD
  const routes: Routes = [
    { path: ':id', component: DummyDetailComponent, outlet: 'detail' }
  ];
  beforeEach(async(() => {
    TestBed.configureTestingModule({ imports: [FormsModule, ReactiveFormsModule, AngularSvgIconModule,
      HttpClientModule, RouterTestingModule.withRoutes(routes), SharedModule, AppModule],
    declarations: [
      WorkbasketOverviewComponent, DummyDetailComponent, ImportExportComponent],
    providers: [
      WorkbasketService,
      WorkbasketDefinitionService,
      OrientationService,
      ImportExportService
    ] })
      .compileComponents();
=======
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketOverviewComponent ]
    })
    .compileComponents();
>>>>>>> TSK-1215 initialized workbasket-overview
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
