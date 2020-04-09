import { Selector } from '@ngxs/store';
import { ClassificationStateModel, ClassificationState } from './classification.state';

export class ClassificationSelectors {
  @Selector([ClassificationState])
  static classificationTypes(state: ClassificationStateModel): string[] {
    return Object.keys(state.classificationTypes);
  }

  @Selector([ClassificationState])
  static selectedClassificationType(state: ClassificationStateModel): string {
    return state.selectedClassificationType;
  }

  @Selector([ClassificationState])
  static selectCategories(state: ClassificationStateModel): string[] {
    return state.classificationTypes[state.selectedClassificationType];
  }

  @Selector([ClassificationState])
  static selectClassificationTypesObject(state: ClassificationStateModel): Object {
    return state.classificationTypes;
  }
}
