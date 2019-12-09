import { Component, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { ViewChild } from '@angular/core';

import { MessageModal } from 'app/models/message-modal';

import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
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
    positionClass: string;

    @Output()
    spinnerIsRunning = new EventEmitter<boolean>();

    @ViewChild('spinnerModal', { static: true })
    private modal;

    constructor(private generalModalService: GeneralModalService) {

    }

    private runSpinner(value) {
        this.currentTimeout = setTimeout(() => {
            if (this.isModal) { $(this.modal.nativeElement).modal('show'); }
            this.isDelayedRunning = value;
            this.cancelTimeout();
            this.requestTimeout = setTimeout(() => {
                this.generalModalService.triggerMessage(
                    new MessageModal('There was an error with your request, please make sure you have internet connection',
                        'Request time execeed'));
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

    ngOnDestroy(): any {
        this.cancelTimeout();
    }
}
