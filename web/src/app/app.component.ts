import { Component, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatSidenav } from '@angular/material';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { SidenavService } from './shared/services/sidenav/sidenav.service';
import { RequestInProgressService } from './shared/services/request-in-progress/request-in-progress.service';
import { OrientationService } from './shared/services/orientation/orientation.service';
import { SelectedRouteService } from './shared/services/selected-route/selected-route';
import { UploadService } from './shared/services/upload/upload.service';
import { ErrorModel } from './shared/models/error-model';
import { TaskanaEngineService } from './shared/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from 'app/shared/services/window/window.service';
import { environment } from 'environments/environment';

@Component({
  selector: 'taskana-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  workbasketsRoute = true;
  selectedRoute = '';

  requestInProgress = false;
  currentProgressValue = 0;

  requestInProgressSubscription: Subscription;
  selectedRouteSubscription: Subscription;
  routerSubscription: Subscription;
  uploadingFileSubscription: Subscription;
  error: ErrorModel;
  version: string;

  constructor(
    private router: Router,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService,
    private selectedRouteService: SelectedRouteService,
    private formsValidatorService: FormsValidatorService,
    public uploadService: UploadService,
    private sidenavService: SidenavService,
    private taskanaEngineService: TaskanaEngineService,
    private window: WindowRefService
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
    });

    this.uploadingFileSubscription = this.uploadService.getCurrentProgressValue().subscribe((value) => {
      this.currentProgressValue = value;
    });

    this.taskanaEngineService.getVersion().subscribe((restVersion) => {
      this.version = restVersion.version;
    });
  }

  logout() {
    this.taskanaEngineService.logout().subscribe(() => {});
    this.window.nativeWindow.location.href = environment.taskanaLogoutUrl;
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
