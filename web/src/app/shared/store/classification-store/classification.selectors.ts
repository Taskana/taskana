import { Selector } from '@ngxs/store';
import { ClassificationStateModel, ClassificationState } from './classification.state';
import { ACTION } from '../../models/action';
import { Classification } from '../../models/classification';
import { CategoriesResponse } from '../../services/classification-categories/classification-categories.service';

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
  static selectClassificationTypesObject(state: ClassificationStateModel): CategoriesResponse {
    return state.classificationTypes;
  }

  @Selector([ClassificationState])
  static classifications(state: ClassificationStateModel): Classification[] {
    return state.classifications;
  }

  @Selector([ClassificationState])
  static selectedClassification(state: ClassificationStateModel): Classification {
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
