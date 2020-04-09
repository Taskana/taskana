import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Store, Select } from '@ngxs/store';
import { ClassificationSelectors } from 'app/store/classification-store/classification.selectors';
import { SetSelectedClassificationType } from 'app/store/classification-store/classification.actions';


@Component({
  selector: 'taskana-classification-types-selector',
  templateUrl: './classification-types-selector.component.html',
  styleUrls: ['./classification-types-selector.component.scss']
})
export class ClassificationTypesSelectorComponent {
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;
  @Select(ClassificationSelectors.classificationTypes) classificationTypes$: Observable<string[]>;

  constructor(private store: Store) {}

  select(value: string): void {
    this.store.dispatch(new SetSelectedClassificationType(value));
  }
}
