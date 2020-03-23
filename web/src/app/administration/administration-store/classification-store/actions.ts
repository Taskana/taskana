import { createAction, props } from '@ngrx/store';

export const loadClassificationTypes = createAction(
  '[Classification-List Component] Load classification types'
);

export const loadClassificationTypesSuccess = createAction(
  '[Classification-List Component] Load classification types success',
  props<{ types: Array<string> }>()
);

export const setSelectedClassificationType = createAction(
  '[Classification-List Component] Set selected classification type ',
  props<{ initalType: string }>()
);

export const setCategories = createAction(
  '[Classification-List Component] Set categories dependent on selected classification type ',
  props<{ categoriesFromSelectedType: string[] }>()
);
