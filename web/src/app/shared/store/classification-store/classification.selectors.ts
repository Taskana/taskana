import { Selector } from '@ngxs/store';
import { ClassificationStateModel, ClassificationState } from './classification.state';
import { ClassificationDefinition } from '../../models/classification-definition';
import { ACTION } from '../../models/action';

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

  @Selector([ClassificationState])
  static classifications(state: ClassificationStateModel): ClassificationDefinition[] {
    return state.classifications;
  }

  @Selector([ClassificationState])
  static selectedClassification(state: ClassificationStateModel): ClassificationDefinition {
    return state.selectedClassification;
  }

  @Selector([ClassificationState])
  static activeAction(state: ClassificationStateModel): ACTION {
    return state.action;
  }

  @Selector([ClassificationState])
  static selectedClassificationId(state: ClassificationStateModel): string {
    return state.selectedClassification.classificationId;
  }
}
