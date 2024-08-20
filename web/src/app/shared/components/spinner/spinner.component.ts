import { Component, EventEmitter, Input, OnDestroy, Output, ViewChild } from '@angular/core';
import { NotificationService } from '../../services/notifications/notification.service';

declare let $: any;

@Component({
  selector: 'kadai-shared-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent implements OnDestroy {
  showSpinner: boolean;
  @Input()
  delay = 0;

  @Input()
  isModal = false;

  @Input()
  positionClass: string;

  @Output()
  spinnerIsRunning = new EventEmitter<boolean>();

  private currentTimeout: any;
  private requestTimeout: any;
  private maxRequestTimeout = 10000;
  @ViewChild('spinnerModal', { static: true })
  private modal;

  constructor(private notificationService: NotificationService) {}

  set isDelayedRunning(value: boolean) {
    this.showSpinner = value;
    this.spinnerIsRunning.next(value);
  }

  @Input()
  set isRunning(value: boolean) {
    if (!value) {
      this.cancelTimeout();
      if (this.isModal) {
        this.closeModal();
      }
      this.isDelayedRunning = false;
      return;
    }

    this.runSpinner(value);
  }

  ngOnDestroy(): any {
    this.cancelTimeout();
  }

  private runSpinner(value) {
    this.currentTimeout = setTimeout(() => {
      if (this.isModal) {
        $(this.modal.nativeElement).modal('show');
      }
      this.isDelayedRunning = value;
      this.cancelTimeout();
      this.requestTimeout = setTimeout(() => {
        this.notificationService.showError('SPINNER_TIMEOUT');
        this.cancelTimeout();
        this.isRunning = false;
      }, this.maxRequestTimeout);
    }, this.delay);
  }

  private closeModal() {
    if (this.showSpinner) {
      $(this.modal.nativeElement).modal('hide');
    }
  }

  private cancelTimeout(): void {
    clearTimeout(this.currentTimeout);
    clearTimeout(this.requestTimeout);
    delete this.currentTimeout; // do we need this?
    delete this.requestTimeout;
  }
}
