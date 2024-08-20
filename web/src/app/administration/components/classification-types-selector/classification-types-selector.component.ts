import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Store, Select } from '@ngxs/store';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { SetSelectedClassificationType } from 'app/shared/store/classification-store/classification.actions';
import { Location } from '@angular/common';

@Component({
  selector: 'kadai-administration-classification-types-selector',
  templateUrl: './classification-types-selector.component.html',
  styleUrls: ['./classification-types-selector.component.scss']
})
export class ClassificationTypesSelectorComponent {
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;
  @Select(ClassificationSelectors.classificationTypes) classificationTypes$: Observable<string[]>;

  constructor(private store: Store, private location: Location) {}

  select(value: string): void {
    this.store.dispatch(new SetSelectedClassificationType(value));
    this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications'));
  }
}
