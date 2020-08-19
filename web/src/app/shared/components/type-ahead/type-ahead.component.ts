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
import { TypeaheadMatch } from 'ngx-bootstrap/typeahead';

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
export class TypeAheadComponent implements AfterViewInit, ControlValueAccessor {
  dataSource: any;
  typing = false;

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
  isRequired = true;

  @Output()
  selectedItem = new EventEmitter<AccessIdDefinition>();

  @Output()
  inputField = new EventEmitter<ElementRef>();

  @ViewChild('inputTypeAhead')
  private inputTypeAhead;

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
      this.onChangeCallback(v);
    }
  }

  // From ControlValueAccessor interface
  writeValue(value: any) {
    if (value !== this.innerValue) {
      this.innerValue = value;
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

  ngAfterViewInit() {
    this.inputField.emit(this.inputTypeAhead);
  }

  initializeDataSource() {
    this.dataSource = new Observable((observer: any) => {
      observer.next(this.value);
    }).pipe(mergeMap((token: string) => this.getUsersAsObservable(token)));
    this.accessIdsService.searchForAccessId(this.value).subscribe((items) => {
      if (items.length > 0) {
        this.dataSource.selected = items.find((item) => item.accessId.toLowerCase() === this.value.toLowerCase());
      }
    });
  }

  getUsersAsObservable(accessId: string): Observable<any> {
    return this.accessIdsService.searchForAccessId(accessId);
  }

  typeaheadOnSelect(event: TypeaheadMatch): void {
    if (event && event.item) {
      this.value = event.item.accessId;
      this.dataSource.selected = event.item;
      this.selectedItem.emit(this.dataSource.selected);
    }
    this.setTyping(false);
  }

  setTyping(value) {
    if (this.disable) {
      return;
    }
    if (value) {
      setTimeout(() => {
        this.inputTypeAhead.nativeElement.focus();
      }, 1);
    }
    this.typing = value;
  }

  changeTypeaheadLoading(e: boolean): void {
    this.typeaheadLoading = e;
  }

  join(text: string, str: string) {
    return text.toLocaleLowerCase().split(str).join(`<strong>${str}</strong>`);
  }
}
