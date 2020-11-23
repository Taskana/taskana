import { Component, Input } from '@angular/core';

@Component({
  selector: 'taskana-shared-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class ProgressSpinnerComponent {
  @Input()
  currentValue = 0;
}
