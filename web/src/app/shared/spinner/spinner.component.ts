import { Component, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { ViewChild } from '@angular/core';

import { ErrorModel } from 'app/models/modal-error';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
declare var $: any;

@Component({
    selector: 'taskana-spinner',
    templateUrl: './spinner.component.html',
    styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent implements OnDestroy {
    private currentTimeout: any;
    private requestTimeout: any;
    private maxRequestTimeout = 10000;

    set isDelayedRunning(value: boolean) {
        this.showSpinner = value;
        this.spinnerIsRunning.next(value)
    }
    showSpinner: boolean;

    @Input()
    delay = 250;

    @Input()
    set isRunning(value: boolean) {
        if (!value) {
            this.cancelTimeout();
            if (this.isModal) { this.closeModal(); }
            this.isDelayedRunning = false;
            return;
        }

        if (this.currentTimeout) {
            return;
        }
        this.runSpinner(value);

    }

    @Input()
    isModal = false;

    @Input()
    positionClass: string = undefined;

    @Output()
    spinnerIsRunning = new EventEmitter<boolean>();

    @ViewChild('spinnerModal')
    private modal;

    constructor(private errorModalService: ErrorModalService) {

    }

    private runSpinner(value) {
        this.currentTimeout = setTimeout(() => {
            if (this.isModal) { $(this.modal.nativeElement).modal('toggle'); }
            this.isDelayedRunning = value;
            this.cancelTimeout();
            this.requestTimeout = setTimeout(() => {
                this.errorModalService.triggerError(
                    new ErrorModel('There was an error with your request, please make sure you have internet connection',
                        'Request time execeed'));
                this.cancelTimeout();
                this.isRunning = false;
            }, this.maxRequestTimeout);
        }, this.delay);
    }
    private closeModal() {
        if (this.showSpinner) {
            $(this.modal.nativeElement).modal('toggle');
        }
    }


    private cancelTimeout(): void {
        clearTimeout(this.currentTimeout);
        clearTimeout(this.requestTimeout);
        this.currentTimeout = undefined;
        this.requestTimeout = undefined;
    }

    ngOnDestroy(): any {
        this.cancelTimeout();
    }
}
