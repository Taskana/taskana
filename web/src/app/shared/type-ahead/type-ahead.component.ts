import { Component, OnInit, Input, EventEmitter, Output, ViewChild, ElementRef, forwardRef } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TypeaheadMatch } from 'ngx-bootstrap/typeahead';

import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { AccessItemsComponent } from 'app/administration/workbasket/details/access-items/access-items.component';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

const noop = () => {
};

@Component({
  selector: 'taskana-type-ahead',
  templateUrl: './type-ahead.component.html',
  styleUrls: ['./type-ahead.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TypeAheadComponent),
      multi: true
    }

  ]

})
export class TypeAheadComponent implements OnInit, ControlValueAccessor {

  dataSource: any;
  typing = false;

  @Input()
  placeHolderMessage;

  @ViewChild('inputTypeAhead')
  private inputTypeAhead;

  typeaheadLoading = false;
  typeaheadMinLength = 2;
  typeaheadWaitMs = 500;
  typeaheadOptionsInScrollableView = 6;

  // The internal data model
  private innerValue: any = '';

  // Placeholders for the callbacks which are later provided
  // by the Control Value Accessor
  private onTouchedCallback: () => void = noop;
  private onChangeCallback: (_: any) => void = noop;

  // get accessor
  get value(): any {
    return this.innerValue;
  };

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

  constructor(private accessIdsService: AccessIdsService) {
  }

  ngOnInit() {

  }

  initializeDataSource() {
    this.dataSource = Observable.create((observer: any) => {
      observer.next(this.value);
    }).mergeMap((token: string) => this.getUsersAsObservable(token));
    this.accessIdsService.getAccessItemsInformation(this.value).subscribe(items => {
      if (items.length > 0) {
        this.dataSource.selected = items.find(item => item.accessId === this.value);
      }
    });
  }


  getUsersAsObservable(token: string): Observable<any> {
    return this.accessIdsService.getAccessItemsInformation(token);
  }

  typeaheadOnSelect(event: TypeaheadMatch): void {
    if (event && event.item) {
      this.value = event.item.accessId;
      this.dataSource.selected = event.item;
    }
    this.setTyping(false);
  }

  setTyping(value) {
    if (value) {
      setTimeout(() => {
        this.inputTypeAhead.nativeElement.focus();
      }, 1)

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
