import { Component, OnInit, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'taskana-shared-number-picker',
  templateUrl: './number-picker.component.html',
  styleUrls: ['./number-picker.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => NumberPickerComponent),
      multi: true
    }
  ]
})
export class NumberPickerComponent implements OnInit, ControlValueAccessor {
  @Input() required = false;

  // The internal data model
  private innerValue: any = 0;

  // Placeholders for the callbacks which are later provided
  // by the Control Value Accessor
  private onTouchedCallback: () => {};
  private onChangeCallback: (_: any) => {};

  ngOnInit() {
  }

  // get accessor
  get value(): any {
    return this.innerValue;
  }

  // set accessor including call the onchange callback
  set value(v: any) {
    if (v !== this.innerValue) {
      this.innerValue = v;
      this.onChangeCallback(v);
    }
  }

  // From ControlValueAccessor interface
  writeValue(value: any) {
    if (value !== this.innerValue) {
      this.innerValue = value;
    }
  }

  // From ControlValueAccessor interface
  registerOnChange(fn: any) {
    this.onChangeCallback = fn;
  }

  // From ControlValueAccessor interface
  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn;
  }

  increase() {
    if (!this.value) {
      this.value = 0;
    }
    this.value += 1;
  }

  decrease() {
    if (!this.value) {
      this.value = 0;
    }
    this.value -= 1;
  }
}
