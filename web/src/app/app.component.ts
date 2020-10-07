import { Component, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatSidenav } from '@angular/material';

import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { SidenavService } from './shared/services/sidenav/sidenav.service';

import { environment } from 'environments/environment';

import { RequestInProgressService } from './shared/services/request-in-progress/request-in-progress.service';
import { OrientationService } from './shared/services/orientation/orientation.service';
import { SelectedRouteService } from './shared/services/selected-route/selected-route';
import { UploadService } from './shared/services/upload/upload.service';
import { ErrorModel } from './shared/models/error-model';
import { NotificationService } from './shared/services/notifications/notification.service';
import { TaskanaEngineService } from './shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from 'app/shared/services/window/window.service';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { UserGuard } from 'app/shared/guards/user.guard';

@Component({
  selector: 'taskana-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  workbasketsRoute = true;

  selectedRoute = '';
  route: string;
  title = '';

  requestInProgress = false;
  currentProgressValue = 0;

  requestInProgressSubscription: Subscription;
  selectedRouteSubscription: Subscription;
  routerSubscription: Subscription;
  uploadingFileSubscription: Subscription;
  error: ErrorModel;

  titleAdministration = 'Administration';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleAccessItems = 'Access items';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  titleHistory = 'History';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;
  version: string;

  adminUrl = 'taskana/administration';
  monitorUrl = 'taskana/monitor';
  workplaceUrl = 'taskana/workplace';
  historyUrl = 'taskana/history';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;
  historyAccess = false;

  constructor(
    private router: Router,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService,
    private selectedRouteService: SelectedRouteService,
    private formsValidatorService: FormsValidatorService,
    private errorService: NotificationService,
    public uploadService: UploadService,
    private taskanaEngineService: TaskanaEngineService,
    private window: WindowRefService,
    private sidenavService: SidenavService
  ) {}

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.orientationService.onResize();
  }

  @ViewChild('sidenav') public sidenav: MatSidenav;

  ngOnInit() {
    this.routerSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.selectedRouteService.selectRoute(event);
        this.formsValidatorService.formSubmitAttempt = false;
      }
    });

    this.requestInProgressSubscription = this.requestInProgressService
      .getRequestInProgress()
      .subscribe((value: boolean) => {
        this.requestInProgress = value;
      });

    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      if (value.indexOf('classifications') !== -1) {
        this.workbasketsRoute = false;
      }
      this.selectedRoute = value;
      this.setTitle(value);
    });
    this.uploadingFileSubscription = this.uploadService.getCurrentProgressValue().subscribe((value) => {
      this.currentProgressValue = value;
    });

    this.taskanaEngineService.getVersion().subscribe((restVersion) => {
      this.version = restVersion.version;
    });

    this.administrationAccess = this.taskanaEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.taskanaEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.taskanaEngineService.hasRole(UserGuard.roles);

    this.taskanaEngineService.isHistoryProviderEnabled().subscribe((value) => {
      this.historyAccess = value;
    });
  }

  logout() {
    this.taskanaEngineService.logout().subscribe(() => {});
    this.window.nativeWindow.location.href = environment.taskanaLogoutUrl;
  }

  private setTitle(value: string = 'workbaskets') {
    if (value.indexOf('workbaskets') === 0) {
      this.title = this.titleWorkbaskets;
    } else if (value.indexOf('classifications') === 0) {
      this.title = this.titleClassifications;
    } else if (value.indexOf('monitor') === 0) {
      this.title = this.titleMonitor;
    } else if (value.indexOf('workplace') === 0) {
      this.title = this.titleWorkplace;
    } else if (value.indexOf('access-items') === 0) {
      this.title = this.titleAccessItems;
    } else if (value.indexOf('history') === 0) {
      this.title = this.titleHistory;
    }
  }

  ngAfterViewInit(): void {
    this.sidenavService.setSidenav(this.sidenav);
  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
    if (this.requestInProgressSubscription) {
      this.requestInProgressSubscription.unsubscribe();
    }
    if (this.selectedRouteSubscription) {
      this.selectedRouteSubscription.unsubscribe();
    }
    if (this.uploadingFileSubscription) {
      this.uploadingFileSubscription.unsubscribe();
    }
  }
}
