import { createReducer, on, ActionCreator } from '@ngrx/store';
import { initialState, State } from './state';
import { loadClassificationTypesSuccess, setSelectedClassificationType, setCategories } from './actions';

const reducer = createReducer(
  initialState,
  on(loadClassificationTypesSuccess, (state, { types }) => ({
    ...state,
    classificationTypes: types
  })),
  on(setSelectedClassificationType, (state, { initalType }) => ({
    ...state,
    selectedClassificationType: initalType
  })),
  on(setCategories, (state, { categoriesFromSelectedType }) => ({
    ...state,
    categories: categoriesFromSelectedType
  })),
);

export function classificationReducer(state: State, action: ActionCreator) {
  return reducer(state, action);
}
