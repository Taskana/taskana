import { Component, Input, ElementRef } from '@angular/core';
import { ViewChild } from '@angular/core';
declare var $: any;

@Component({
    selector: 'taskana-spinner',
    templateUrl: './spinner.component.html',
    styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent {
    private currentTimeout: any;

    isDelayedRunning: boolean = false;

    @Input()
    delay: number = 300;

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
    

    @ViewChild('spinnerModal')
    private modal;

    private runSpinner(value) {
        this.currentTimeout = setTimeout(() => {
            if (this.isModal) { $(this.modal.nativeElement).modal('toggle'); }
            this.isDelayedRunning = value;
            this.cancelTimeout();
        }, this.delay);
    }
    private closeModal() {
        if (this.isDelayedRunning) {
            $(this.modal.nativeElement).modal('toggle');
        }
    }


    private cancelTimeout(): void {
        clearTimeout(this.currentTimeout);
        this.currentTimeout = undefined;
    }

    ngOnDestroy(): any {
        this.cancelTimeout();
    }
}