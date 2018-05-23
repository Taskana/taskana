import { Routes } from '@angular/router';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'app/shared/shared.module';
import { Observable } from 'rxjs/Observable';

import { NavBarComponent } from './nav-bar.component';
import { UserInformationComponent } from 'app/components/user-information/user-information.component';

import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { DomainService } from 'app/services/domain/domain.service';
import { DomainServiceMock } from 'app/services/domain/domain.service.mock';
import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from 'app/services/window/window.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

import { UserInfoModel } from 'app/models/user-info';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let debugElement, navBar, taskanaEngineService;

  const routes: Routes = [
    { path: 'classifications', component: NavBarComponent }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavBarComponent, UserInformationComponent],
      imports: [
        AngularSvgIconModule,
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        SharedModule
      ],
      providers: [SelectedRouteService, {
        provide: DomainService,
        useClass: DomainServiceMock
      },
        BusinessAdminGuard,
        MonitorGuard,
        TaskanaEngineService,
        WindowRefService,
        ErrorModalService,
        RequestInProgressService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavBarComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    navBar = fixture.debugElement.componentInstance;
    taskanaEngineService = TestBed.get(TaskanaEngineService);
    spyOn(taskanaEngineService, 'getUserInformation').and.returnValue(Observable.of(new UserInfoModel));
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.detectChanges()
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title ''`, (() => {
    expect(navBar.title).toEqual('');
  }));

});
