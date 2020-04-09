import { Action, State, StateContext } from '@ngxs/store';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CategoriesResponse, ClassificationCategoriesService } from '../../shared/services/classifications/classification-categories.service';
import { SetSelectedClassificationType } from './classification.actions';

class InitializeStore {
  static readonly type = '[ClassificationState] Initializing state';
}

@State<ClassificationStateModel>({ name: 'classification' })
export class ClassificationState {
  constructor(private categoryService: ClassificationCategoriesService) {
  }

  @Action(SetSelectedClassificationType)
  setSelectedClassificationType(ctx: StateContext<ClassificationStateModel>, action: SetSelectedClassificationType): void {
    const state: ClassificationStateModel = ctx.getState();
    if (state.classificationTypes[action.selectedType]) {
      ctx.patchState({ selectedClassificationType: action.selectedType });
    }
  }

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    return this.categoryService.getClassificationCategoriesByType().pipe(
      tap(classificationTypes => {
        ctx.setState({
          ...ctx.getState(),
          classificationTypes,
          selectedClassificationType: Object.keys(classificationTypes)[0],
        });
      }),
    );
  }

  ngxsOnInit(ctx: StateContext<ClassificationStateModel>): void {
    ctx.dispatch(new InitializeStore());
  }
}

export interface ClassificationStateModel {
  selectedClassificationType: string;
  classificationTypes: CategoriesResponse,
}
