import { Component, Input, ElementRef, Output, EventEmitter } from '@angular/core';
import { ViewChild } from '@angular/core';
declare var $: any;

@Component({
    selector: 'taskana-spinner',
    templateUrl: './spinner.component.html',
    styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent {
    private currentTimeout: any;
    private requestTimeout: any;
    private maxRequestTimeout: number = 10000;

    isDelayedRunning: boolean = false;

    @Input()
    delay: number = 200;

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
    isModal: boolean = false;

    @Input()
    positionClass: string = undefined;

    @Output()
    requestTimeoutExceeded = new EventEmitter<string>()

    @ViewChild('spinnerModal')
    private modal;

    private runSpinner(value) {
        this.currentTimeout = setTimeout(() => {
            if (this.isModal) { $(this.modal.nativeElement).modal('toggle'); }
                this.isDelayedRunning = value;
                this.cancelTimeout();
            this.requestTimeout = setTimeout(() => {
                this.requestTimeoutExceeded.emit('There was an error with your request, please make sure you have internet connection');
                this.cancelTimeout();
                this.isRunning = false;
            },this.maxRequestTimeout);
        }, this.delay);
    }
    private closeModal() {
        if (this.isDelayedRunning) {
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