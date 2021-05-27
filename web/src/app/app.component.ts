import { Component, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { Subject } from 'rxjs';
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
import { MatSidenav } from '@angular/material/sidenav';
import { takeUntil } from 'rxjs/operators';

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

  error: ErrorModel;
  version: string;
  toggle: boolean = false;

  destroy$ = new Subject<void>();

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
    this.router.events.pipe(takeUntil(this.destroy$)).subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.selectedRouteService.selectRoute(event);
        this.formsValidatorService.formSubmitAttempt = false;
      }
    });

    this.requestInProgressService
      .getRequestInProgress()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value: boolean) => {
        this.requestInProgress = value;
      });

    this.selectedRouteService
      .getSelectedRoute()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value: string) => {
        if (value.indexOf('classifications') !== -1) {
          this.workbasketsRoute = false;
        }
        this.selectedRoute = value;
      });

    this.uploadService
      .getCurrentProgressObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.currentProgressValue = value;
      });

    this.taskanaEngineService
      .getVersion()
      .pipe(takeUntil(this.destroy$))
      .subscribe((restVersion) => {
        this.version = restVersion.version;
      });
  }

  logout() {
    this.taskanaEngineService.logout();
    this.window.nativeWindow.location.href = environment.taskanaLogoutUrl;
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    this.sidenavService.toggleSidenav();
  }

  ngAfterViewInit(): void {
    this.sidenavService.setSidenav(this.sidenav);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
