import { Component, Input, ViewChild, forwardRef, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';

import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { highlight } from 'app/shared/animations/validation.animation';
import { mergeMap } from 'rxjs/operators';
import { AccessIdDefinition } from 'app/shared/models/access-id';

@Component({
  selector: 'taskana-shared-type-ahead',
  templateUrl: './type-ahead.component.html',
  styleUrls: ['./type-ahead.component.scss'],
  animations: [highlight],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TypeAheadComponent),
      multi: true
    }
  ]
})
export class TypeAheadComponent implements ControlValueAccessor {
  dataSource: any;
  typing = false;
  isFirst = false;
  items = [];

  @Input()
  placeHolderMessage;

  @Input()
  validationValue;

  @Input()
  displayError;

  @Input()
  width;

  @Input()
  disable;

  @Input()
  isRequired;

  @Output()
  selectedItem = new EventEmitter<AccessIdDefinition>();

  @ViewChild('inputTypeAhead')
  typeaheadLoading = false;
  typeaheadMinLength = 3;
  typeaheadWaitMs = 500;
  typeaheadOptionsInScrollableView = 6;

  // The internal data model
  private innerValue: any;

  // Placeholders for the callbacks which are later provided
  // by the Control Value Accessor
  private onTouchedCallback: () => {};
  private onChangeCallback: (_: any) => {};

  // get accessor
  get value(): any {
    return this.innerValue;
  }

  // set accessor including call the onchange callback
  set value(v: any) {
    if (v !== this.innerValue) {
      this.innerValue = v;
    }
  }

  // From ControlValueAccessor interface
  writeValue(value: any) {
    if (value !== this.innerValue) {
      this.innerValue = value;
      if (this.value) {
        this.isFirst = true;
      }
      this.initializeDataSource();
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

  constructor(private accessIdsService: AccessIdsService) {}

  initializeDataSource() {
    this.dataSource = new Observable((observer: any) => {
      observer.next(this.value);
    }).pipe(mergeMap((token: string) => this.getUsersAsObservable(token)));
    this.accessIdsService.searchForAccessId(this.value).subscribe((items) => {
      this.items = items;
      if (this.isFirst) {
        this.dataSource.selected = this.items.find((item) => item.accessId.toLowerCase() === this.value.toLowerCase());
        this.selectedItem.emit(this.dataSource.selected);
      }
    });
  }

  getUsersAsObservable(accessId: string): Observable<any> {
    return this.accessIdsService.searchForAccessId(accessId);
  }

  typeaheadOnSelect(event): void {
    if (event) {
      if (this.items.length > 0) {
        this.dataSource.selected = this.items.find((item) => item.accessId.toLowerCase() === this.value.toLowerCase());
      }
      this.selectedItem.emit(this.dataSource.selected);
    }
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }
  }
}
