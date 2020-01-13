import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs';

import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { MessageModal } from './models/message-modal';

import { GeneralModalService } from './services/general-modal/general-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { OrientationService } from './services/orientation/orientation.service';
import { SelectedRouteService } from './services/selected-route/selected-route';
import { UploadService } from './shared/services/upload/upload.service';

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

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.orientationService.onResize();
  }

  constructor(
    private router: Router,
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService,
    private orientationService: OrientationService,
    private selectedRouteService: SelectedRouteService,
    private formsValidatorService: FormsValidatorService,
    public uploadService: UploadService
) {
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
    if (this.routerSubscription) { this.routerSubscription.unsubscribe(); }
    if (this.modalSubscription) { this.modalSubscription.unsubscribe(); }
    if (this.requestInProgressSubscription) { this.requestInProgressSubscription.unsubscribe(); }
    if (this.selectedRouteSubscription) { this.selectedRouteSubscription.unsubscribe(); }
    if (this.uploadingFileSubscription) { this.uploadingFileSubscription.unsubscribe(); }
  }
}
