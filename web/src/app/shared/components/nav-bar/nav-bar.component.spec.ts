import { Routes } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'app/shared/shared.module';

import { UserInformationComponent } from 'app/shared/components/user-information/user-information.component';

import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { WindowRefService } from 'app/shared/services/window/window.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

import { configureTests } from 'app/app.test.configuration';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Overlay } from '@angular/cdk/overlay';
import { NavBarComponent } from './nav-bar.component';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let debugElement;
  let navBar;

  const routes: Routes = [{ path: 'classifications', component: NavBarComponent }];

  beforeEach((done) => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [NavBarComponent, UserInformationComponent],
        imports: [AngularSvgIconModule, HttpClientModule, RouterTestingModule.withRoutes(routes), SharedModule],
        providers: [
          SelectedRouteService,
          BusinessAdminGuard,
          MonitorGuard,
          WindowRefService,
          RequestInProgressService,
          MatSnackBar,
          Overlay
        ]
      });
    };
    configureTests(configure).then((testBed) => {
      fixture = TestBed.createComponent(NavBarComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      navBar = fixture.debugElement.componentInstance;
      fixture.detectChanges();
      done();
    });
  });

  afterEach(() => {
    fixture.detectChanges();
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should have as title ''", () => {
    expect(navBar.title).toEqual('');
  });
});
