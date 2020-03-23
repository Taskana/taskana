import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import { ClassificationStoreActions, ClassificationStoreSelectors, AdministrationState } from '../../administration/administration-store';


@Component({
  selector: 'taskana-classification-types-selector',
  templateUrl: './classification-types-selector.component.html',
  styleUrls: ['./classification-types-selector.component.scss']
})
export class ClassificationTypesSelectorComponent implements OnInit {
  @Input()
  classificationTypes: Array<string> = [];

  classificationTypeSelected: string;

  @Output()
  classificationTypeSelectedChange = new EventEmitter<string>();

  @Output()
  classificationTypeChanged = new EventEmitter<string>();

  classificationTypeSelected$: Observable<string>;
  classificationTypeSelectedSubscription: Subscription;

  constructor(
    private store$: Store<AdministrationState.State>,
  ) {}

  ngOnInit(): void {
    this.classificationTypeSelected$ = this.store$.select(ClassificationStoreSelectors.selectSelectedClassificationType);
    this.store$.dispatch(ClassificationStoreActions.loadClassificationTypes());
    // this should only be temporary until more actions are implemented
    this.classificationTypeSelectedSubscription = this.classificationTypeSelected$.subscribe(
      selected => { this.classificationTypeSelected = selected; }
    );
  }

  select(value: string): void {
    this.classificationTypeSelected = value;
    this.classificationTypeChanged.emit(value);
  }

  ngOnDestroy(): void {
    if (this.classificationTypeSelectedSubscription) { this.classificationTypeSelectedSubscription.unsubscribe(); }
  }
}
