import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { mergeMap, take, tap } from 'rxjs/operators';
import { KadaiDate } from 'app/shared/util/kadai.date';
import {
  CategoriesResponse,
  ClassificationCategoriesService
} from '../../services/classification-categories/classification-categories.service';
import {
  CopyClassification,
  CreateClassification,
  DeselectClassification,
  GetClassifications,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveCreatedClassification,
  SaveModifiedClassification,
  SelectClassification,
  SetSelectedClassificationType,
  UpdateClassification
} from './classification.actions';
import { ClassificationsService } from '../../services/classifications/classifications.service';
import { DomainService } from '../../services/domain/domain.service';
import { Classification } from '../../models/classification';
import { ClassificationSummary } from '../../models/classification-summary';
import { ClassificationQueryFilterParameter } from '../../models/classification-query-filter-parameter';
import { ClassificationQuerySortParameter, Direction, Sorting } from '../../models/sorting';
import { Injectable } from '@angular/core';

class InitializeStore {
  static readonly type = '[ClassificationState] Initializing state';
}

@Injectable()
@State<ClassificationStateModel>({ name: 'classification' })
export class ClassificationState implements NgxsAfterBootstrap {
  constructor(
    private categoryService: ClassificationCategoriesService,
    private classificationsService: ClassificationsService,
    private domainService: DomainService
  ) {}

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    return this.categoryService.getClassificationCategoriesByType().pipe(
      take(1),
      tap((classificationTypes) => {
        ctx.patchState({
          classificationTypes,
          classifications: undefined,
          selectedClassificationType: Object.keys(classificationTypes)[0]
        });
      })
    );
  }

  @Action(SetSelectedClassificationType)
  setSelectedClassificationType(
    ctx: StateContext<ClassificationStateModel>,
    action: SetSelectedClassificationType
  ): Observable<null> {
    if (ctx.getState().classificationTypes[action.selectedType]) {
      ctx.patchState({
        selectedClassificationType: action.selectedType,
        selectedClassification: undefined
      });
    }
    return of(null);
  }

  @Action(SelectClassification)
  selectClassification(
    ctx: StateContext<ClassificationStateModel>,
    action: SelectClassification
  ): Observable<Classification | null> {
    if (typeof action.classificationId !== 'undefined') {
      return this.classificationsService.getClassification(action.classificationId).pipe(
        take(1),
        tap((selectedClassification) =>
          ctx.patchState({
            selectedClassification,
            selectedClassificationType: selectedClassification.type
          })
        )
      );
    }
    return of(null);
  }

  @Action(DeselectClassification)
  deselectClassification(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    ctx.patchState({
      selectedClassification: undefined
    });
    return of(null);
  }

  @Action(GetClassifications)
  getClassifications(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const { selectedClassificationType } = ctx.getState();
    return this.domainService.getSelectedDomain().pipe(
      take(1),
      mergeMap((domain) => {
        const filter: ClassificationQueryFilterParameter = {
          domain: [domain],
          type: [selectedClassificationType]
        };
        const sort: Sorting<ClassificationQuerySortParameter> = {
          'sort-by': ClassificationQuerySortParameter.KEY,
          order: Direction.ASC
        };
        return this.classificationsService.getClassifications(filter, sort).pipe(
          take(1),
          tap((list) => ctx.patchState({ classifications: list.classifications }))
        );
      }),
      tap(() => this.domainService.domainChangedComplete())
    );
  }

  @Action(SaveCreatedClassification)
  // this action is called when a copied or a new classification is saved
  createClassification(
    ctx: StateContext<ClassificationStateModel>,
    action: SaveCreatedClassification
  ): Observable<any> {
    return this.classificationsService.postClassification(action.classification).pipe(
      take(1),
      tap((classification) =>
        ctx.patchState({
          classifications: [...ctx.getState().classifications, classification],
          selectedClassification: classification
        })
      )
    );
  }

  @Action(SaveModifiedClassification)
  saveClassification(ctx: StateContext<ClassificationStateModel>, action: SaveModifiedClassification): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      take(1),
      tap((savedClassification) =>
        ctx.patchState({
          classifications: updateClassificationList(ctx.getState().classifications, savedClassification),
          selectedClassification: savedClassification
        })
      )
    );
  }

  @Action(RestoreSelectedClassification)
  restoreSelectedClassification(
    ctx: StateContext<ClassificationStateModel>,
    action: RestoreSelectedClassification
  ): Observable<any> {
    const state = ctx.getState();

    // check whether the classification already exists
    // returns true in case the classification was edited or copied
    if (state.classifications.some((classification) => classification.classificationId === action.classificationId)) {
      return this.classificationsService.getClassification(action.classificationId).pipe(
        take(1),
        tap((selectedClassification) => {
          ctx.patchState({ selectedClassification });
        })
      );
    }

    // the classification is restored to a new classification
    const category = state.classificationTypes[state.selectedClassificationType][0];
    const { type, created, modified, domain, parentId, parentKey } = state.selectedClassification;
    ctx.patchState({ selectedClassification: { type, created, category, modified, domain, parentId, parentKey } });

    return of(null);
  }

  @Action(CreateClassification)
  newCreateClassification(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    // Initialization of a new classification
    const state: ClassificationStateModel = ctx.getState();
    const date = KadaiDate.getDate();
    const initialClassification: Classification = {
      type: state.selectedClassificationType,
      category: state.classificationTypes[state.selectedClassificationType][0],
      created: date,
      modified: date,
      domain: this.domainService.getSelectedDomainValue()
    };
    if (state.selectedClassification) {
      initialClassification.parentId = state.selectedClassification.classificationId;
      initialClassification.parentKey = state.selectedClassification.key;
    }
    ctx.patchState({
      selectedClassification: initialClassification,
      badgeMessage: 'Creating new classification'
    });
    return of(null);
  }

  @Action(CopyClassification)
  newCopyClassification(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    const copy = { ...ctx.getState().selectedClassification };
    copy.key = null;
    copy.classificationId = undefined;
    ctx.patchState({
      selectedClassification: copy,
      badgeMessage: `Copying Classification: ${copy.name}`
    });
    return of(null);
  }

  @Action(RemoveSelectedClassification)
  removeSelectedClassification(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const sel = ctx.getState().selectedClassification;
    return this.classificationsService.deleteClassification(sel.classificationId).pipe(
      take(1),
      tap(() => {
        const classifications = ctx
          .getState()
          .classifications.filter((el) => el.classificationId !== sel.classificationId);
        ctx.patchState({ selectedClassification: undefined, classifications });
      })
    );
  }

  @Action(UpdateClassification)
  updateClassification(
    ctx: StateContext<ClassificationStateModel>,
    action: SaveModifiedClassification
  ): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      take(1),
      tap((classification) => {
        const state = ctx.getState();
        let { selectedClassification } = state;
        if (selectedClassification && selectedClassification.classificationId === classification.classificationId) {
          selectedClassification = classification;
        }
        ctx.patchState({
          classifications: updateClassificationList(state.classifications, classification),
          selectedClassification
        });
      })
    );
  }

  // initialize after Startup service has configured the kadaiRestUrl properly.
  ngxsAfterBootstrap(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    ctx.dispatch(new InitializeStore());
    return of(null);
  }
}

function updateClassificationList(classifications: ClassificationSummary[], classification: Classification) {
  return classifications.map((c) => {
    if (c.classificationId === classification.classificationId) {
      return classification;
    }
    return c;
  });
}

export interface ClassificationStateModel {
  classifications: ClassificationSummary[];
  selectedClassification: Classification;
  selectedClassificationType: string;
  classificationTypes: CategoriesResponse;
  badgeMessage: string;
}
