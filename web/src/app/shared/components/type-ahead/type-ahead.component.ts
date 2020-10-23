import {
  Component,
  Input,
  ViewChild,
  forwardRef,
  Output,
  EventEmitter,
  ElementRef,
  AfterViewInit
} from '@angular/core';
import { Observable } from 'rxjs';

import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { ControlValueAccessor, FormControl, FormGroup, NG_VALUE_ACCESSOR } from '@angular/forms';
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
export class TypeAheadComponent implements AfterViewInit, ControlValueAccessor {
  dataSource: any;
  typing = false;

  _value = '';
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

  @Output()
  inputField = new EventEmitter<ElementRef>();

  @ViewChild('inputTypeAhead')
  private inputTypeAhead;
  private input;

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
      this.initializeDataSource();
    }
  }

  changeValue() {
    this.initializeDataSource();
  }

  // From ControlValueAccessor interface
  registerOnChange(fn: any) {
    this.onChangeCallback = fn;
  }

  // From ControlValueAccessor interface
  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn;
  }

  constructor(private accessIdsService: AccessIdsService, private el: ElementRef) {}

  ngAfterViewInit() {
    this.inputField.emit(this.inputTypeAhead);
  }

  initializeDataSource() {
    this.dataSource = new Observable((observer: any) => {
      observer.next(this.value);
    }).pipe(mergeMap((token: string) => this.getUsersAsObservable(token)));
    this.accessIdsService.searchForAccessId(this.value).subscribe((items) => {
      this.items = items;
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
