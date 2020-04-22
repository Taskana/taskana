import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { MessageModal } from './shared/models/message-modal';

import { GeneralModalService } from './shared/services/general-modal/general-modal.service';
import { RequestInProgressService } from './shared/services/request-in-progress/request-in-progress.service';
import { OrientationService } from './shared/services/orientation/orientation.service';
import { SelectedRouteService } from './shared/services/selected-route/selected-route';
import { UploadService } from './shared/services/upload/upload.service';
import { ErrorModel } from './shared/models/error-model';
import { NotificationService } from './shared/services/notifications/notification.service';

@Component({
  selector: 'taskana-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  workbasketsRoute = true;

  modalMessage = '';
  modalTitle = '';
  modalType;
  selectedRoute = '';

  requestInProgress = false;
  currentProgressValue = 0;

  modalSubscription: Subscription;
  requestInProgressSubscription: Subscription;
  selectedRouteSubscription: Subscription;
  routerSubscription: Subscription;
  uploadingFileSubscription: Subscription;
  error: ErrorModel;

  constructor(
    private router: Router,
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService,
    private selectedRouteService: SelectedRouteService,
    private formsValidatorService: FormsValidatorService,
    private errorService: NotificationService,
    public uploadService: UploadService
  ) {

  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.orientationService.onResize();
  }

  ngOnInit() {
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.selectedRouteService.selectRoute(event);
        this.formsValidatorService.formSubmitAttempt = false;
      }
    });

    this.modalSubscription = this.generalModalService.getMessage().subscribe((messageModal: MessageModal) => {
      if (typeof messageModal.message === 'string') {
        this.modalMessage = messageModal.message;
      } else if (messageModal.message.error instanceof ProgressEvent) {
        this.modalMessage = messageModal.message.message;
      } else {
        this.modalMessage = messageModal.message.error
          ? (`${messageModal.message.error.error} ${messageModal.message.error.message}`)
          : messageModal.message.message;
      }
      this.modalTitle = messageModal.title;
      this.modalType = messageModal.type;
    });

    this.requestInProgressSubscription = this.requestInProgressService.getRequestInProgress().subscribe((value: boolean) => {
      this.requestInProgress = value;
    });

    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      if (value.indexOf('classifications') !== -1) {
        this.workbasketsRoute = false;
      }
      this.selectedRoute = value;
    });
    this.uploadingFileSubscription = this.uploadService.getCurrentProgressValue().subscribe(value => {
      this.currentProgressValue = value;
    });
  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
    if (this.modalSubscription) {
      this.modalSubscription.unsubscribe();
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
