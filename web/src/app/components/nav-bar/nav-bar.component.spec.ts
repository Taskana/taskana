import { Routes } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'app/shared/shared.module';

import { NavBarComponent } from './nav-bar.component';
import { UserInformationComponent } from 'app/components/user-information/user-information.component';

import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { BusinessAdminGuard } from 'app/guards/business-admin-guard';
import { MonitorGuard } from 'app/guards/monitor-guard';
import { WindowRefService } from 'app/services/window/window.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

import { configureTests } from 'app/app.test.configuration';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let debugElement, navBar;

  const routes: Routes = [
    { path: 'classifications', component: NavBarComponent }
  ];

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [NavBarComponent, UserInformationComponent],
        imports: [
          AngularSvgIconModule,
          HttpClientModule,
          RouterTestingModule.withRoutes(routes),
          SharedModule
        ],
        providers: [
          SelectedRouteService,
          BusinessAdminGuard,
          MonitorGuard,
          WindowRefService,
          ErrorModalService,
          RequestInProgressService]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(NavBarComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      navBar = fixture.debugElement.componentInstance;
      fixture.detectChanges();
      done();
    });

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
