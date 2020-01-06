import { Component, OnInit, Input, SimpleChanges, OnChanges } from '@angular/core';
@Component({
  selector: 'taskana-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss']
})
export class ProgressBarComponent implements OnInit, OnChanges {

  @Input()
  currentValue = 0;

  @Input()
  min = 0;

  @Input()
  max = 100;

  inProgress = false;

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.inProgress && changes.currentValue.currentValue > this.min) {
      this.inProgress = true;
    }
    if (this.inProgress && changes.currentValue.currentValue >= this.max) {
      this.inProgress = false;
    }
  }

}
