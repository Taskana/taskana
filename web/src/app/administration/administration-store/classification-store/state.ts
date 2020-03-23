export interface State {
  classificationTypes: string[];
  selectedClassificationType: string;
  categories: string[];
}

export const initialState: State = {
  classificationTypes: [],
  selectedClassificationType: '',
  categories: [],
};
