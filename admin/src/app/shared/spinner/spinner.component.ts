import { Component, Input } from '@angular/core';

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
          this.isDelayedRunning = false;
          return;
      }

      if (this.currentTimeout) {
          return;
      }

      this.currentTimeout = setTimeout(() => {
          this.isDelayedRunning = value;
          this.cancelTimeout();
      }, this.delay);
  }

  private cancelTimeout(): void {
      clearTimeout(this.currentTimeout);
      this.currentTimeout = undefined;
  }

  ngOnDestroy(): any {
      this.cancelTimeout();
  }
}