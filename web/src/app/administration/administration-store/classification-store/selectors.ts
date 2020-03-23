import { createFeatureSelector, createSelector, MemoizedSelector } from '@ngrx/store';
import { State } from './state';

export const selectClassificationState: MemoizedSelector<object, State> = createFeatureSelector<State>('Classification');

export const selectClassificationTypes: MemoizedSelector<object, string[]> = createSelector(
  selectClassificationState, state => state.classificationTypes
);


export const selectSelectedClassificationType: MemoizedSelector<object, string> = createSelector(
  selectClassificationState, state => state.selectedClassificationType
);

export const selectCategories: MemoizedSelector<object, string[]> = createSelector(
  selectClassificationState, state => state.categories
);
