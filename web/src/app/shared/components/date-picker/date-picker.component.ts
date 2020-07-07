import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

@Component({
  selector: 'taskana-shared-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.scss']
})
export class DatePickerComponent implements OnInit {
  @Input() placeholder: string;
  @Input() value: string;
  @Input() id: string;
  @Input() name: string;
  @Output() dateOutput = new EventEmitter<string>();

  valueDate: Date;

  ngOnInit(): void {
    this.valueDate = this.value ? new Date(this.value) : undefined;
  }

  dateChange(newValue: Date) {
    if (newValue) {
      this.dateOutput.emit(newValue.toISOString());
    }
  }
}
