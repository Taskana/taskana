import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { Observable } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { CategoriesResponse,
  ClassificationCategoriesService } from '../../services/classification-categories/classification-categories.service';
import { CreateClassification,
  DeselectClassification,
  GetClassifications,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveClassification,
  SelectClassification,
  SetActiveAction,
  SetSelectedClassificationType,
  UpdateClassification } from './classification.actions';
import { ClassificationsService } from '../../services/classifications/classifications.service';
import { ClassificationDefinition } from '../../models/classification-definition';
import { ACTION } from '../../models/action';

class InitializeStore {
  static readonly type = '[ClassificationState] Initializing state';
}

@State<ClassificationStateModel>({ name: 'classification' })
export class ClassificationState implements NgxsAfterBootstrap {
  constructor(private categoryService: ClassificationCategoriesService,
    private classificationsService: ClassificationsService) {
  }

  @Action(SetSelectedClassificationType)
  setSelectedClassificationType(ctx: StateContext<ClassificationStateModel>, action: SetSelectedClassificationType): void {
    const state: ClassificationStateModel = ctx.getState();
    if (state.classificationTypes[action.selectedType]) {
      ctx.patchState({ selectedClassificationType: action.selectedType });
    }
  }

  @Action(SelectClassification)
  selectClassification(ctx: StateContext<ClassificationStateModel>, action: SelectClassification): Observable<any> | void {
    if (typeof action.classificationId !== 'undefined') {
      return this.classificationsService.getClassification(action.classificationId).pipe(take(1), tap(
        selectedClassification => {
          ctx.patchState({
            selectedClassification,
            action: null
          });
        }
      ));
    }
    return null;
  }

  @Action(DeselectClassification)
  deselectClassification(ctx: StateContext<ClassificationStateModel>): Observable<any> | void {
    ctx.patchState({
      selectedClassification: undefined,
      action: null
    });
    return null;
  }

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    return this.categoryService.getClassificationCategoriesByType().pipe(
      take(1), tap(classificationTypes => {
        ctx.setState({
          ...ctx.getState(),
          classificationTypes,
          classifications: undefined,
          selectedClassificationType: Object.keys(classificationTypes)[0],
        });
      }),
    );
  }

  @Action(GetClassifications)
  getClassifications(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const { selectedClassificationType } = ctx.getState();
    return this.classificationsService.getClassifications(selectedClassificationType).pipe(
      take(1), tap(classifications => {
        classifications.forEach(classification => {
          classification.children = !classification.children ? [] : classification.children;
        });
        ctx.patchState({
          classifications: [...classifications]
        });
      }),
    );
  }

  @Action(CreateClassification)
  createClassification(ctx: StateContext<ClassificationStateModel>, action: CreateClassification): Observable<any> {
    return this.classificationsService.postClassification(action.classification).pipe(
      take(1), tap(classification => {
        ctx.patchState(
          {
            classifications: [...ctx.getState().classifications, classification],
            selectedClassification: classification,
            action: null
          }
        );
      })
    );
  }

  @Action(SaveClassification)
  saveClassification(ctx: StateContext<ClassificationStateModel>, action: SaveClassification): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      // TODO remove this call when backend is fixed modified dates are not same
      take(1), tap(retClassification => this.classificationsService.getClassification(retClassification.classificationId).subscribe(
        savedClassification => {
          ctx.patchState({
            classifications: ctx.getState().classifications.map(currentClassification => {
              if (currentClassification.classificationId === savedClassification.classificationId) { // TODO there has to be a better way
                return savedClassification;
              }
              return currentClassification;
            }),
            selectedClassification: savedClassification
          });
        }
      )), tap(() => this.classificationsService.getClassifications(
        ctx.getState().selectedClassificationType
      ).subscribe( // TODO find a better way because 3 calls are way too much
        classifications => {
          ctx.patchState({
            classifications
          });
        }
      ))
    );
  }

  @Action(RestoreSelectedClassification)
  restoreSelectedClassification(ctx: StateContext<ClassificationStateModel>, action: RestoreSelectedClassification): Observable<any> {
    return this.classificationsService.getClassification(action.classificationId).pipe(
      take(1), tap(selectedClassification => {
        ctx.patchState({ selectedClassification });
      })
    );
  }

  @Action(SetActiveAction)
  setActiveAction(ctx: StateContext<ClassificationStateModel>, action: SetActiveAction): void {
    if (action.action === ACTION.CREATE) {
      ctx.patchState({ selectedClassification: new ClassificationDefinition(), action: action.action });
    } else {
      ctx.patchState({ action: action.action });
    }
  }

  @Action(RemoveSelectedClassification)
  removeSelectedClassification(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const sel = ctx.getState().selectedClassification;
    return this.classificationsService.deleteClassification(sel.classificationId).pipe(take(1), tap(() => {
      const classifications = ctx.getState().classifications.filter(el => el.classificationId !== sel.classificationId);
      ctx.patchState({ selectedClassification: undefined, classifications });
    }));
  }

  @Action(UpdateClassification)
  updateClassification(ctx: StateContext<ClassificationStateModel>, action: SaveClassification): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      // TODO remove this call when backend is fixed modified dates are not same
      take(1), tap(() => this.classificationsService.getClassifications(ctx.getState().selectedClassificationType).subscribe(
        classifications => {
          ctx.patchState({
            classifications
          });
        }
      ))
    );
  }

  // initialize after Startup service has configured the taskanaRestUrl properly.
  ngxsAfterBootstrap(ctx: StateContext<ClassificationStateModel>): void {
    ctx.dispatch(new InitializeStore());
  }
}

export interface ClassificationStateModel {
  classifications: ClassificationDefinition[],
  selectedClassification: ClassificationDefinition,
  selectedClassificationType: string;
  classificationTypes: CategoriesResponse,
  action: ACTION,
}
