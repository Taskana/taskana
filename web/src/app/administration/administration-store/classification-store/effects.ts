import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { map, switchMap, mergeMap } from 'rxjs/operators';

import { ClassificationCategoriesService } from '../../../shared/services/classifications/classification-categories.service';

import * as classificationActions from './actions';

@Injectable()
export class ClassificationEffects {
  constructor(
    private actions$: Actions,
    private categoryService: ClassificationCategoriesService
  ) {}

  loadClassificationTypes$ = createEffect(() => this.actions$.pipe(
    ofType(classificationActions.loadClassificationTypes),
    switchMap(() => this.categoryService.getClassificationTypes()
      .pipe(
        mergeMap(
          (classificationTypes: Array<string>) => [
            classificationActions.loadClassificationTypesSuccess({ types: classificationTypes }),
            classificationActions.setSelectedClassificationType({ initalType: classificationTypes[0] })]
        )
      ))
  ));

  setSelectedClassificationType$ = createEffect(() => this.actions$.pipe(
    ofType(classificationActions.setSelectedClassificationType),
    map(action => action.initalType),
    switchMap(initalType => this.categoryService.getCategories(initalType)
      .pipe(
        map(
          (categories: Array<string>) => classificationActions.setCategories({ categoriesFromSelectedType: categories })
        )
      ))
  ));
}
