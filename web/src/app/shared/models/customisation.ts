import { map } from 'rxjs/operators';
import { OperatorFunction } from 'rxjs';

export interface Customisation {
  [language: string]: CustomisationContent;
}

export interface CustomisationContent {
  global?: GlobalCustomisation;
  workbaskets?: WorkbasketsCustomisation;
  classifications?: ClassificationsCustomisation;
  tasks?: TasksCustomisation;
}

export interface TasksCustomisation {
  information?: {
    owner: LookupField;
  };
}

export interface ClassificationsCustomisation {
  information?: CustomFields;
  categories?: ClassificationCategoryImages;
}

export interface ClassificationCategoryImages {
  [key: string]: string;
}

export interface WorkbasketsCustomisation {
  information?: { owner: LookupField } & CustomFields;
  'access-items'?: AccessItemsCustomisation;
}

export interface GlobalCustomisation {
  debounceTimeLookupField: number;
}

export type AccessItemsCustomisation = { accessId?: LookupField } & CustomFields;

export interface CustomFields {
  [key: string]: CustomField;
}

export interface CustomField {
  visible: boolean;
  field: string;
}

export interface LookupField {
  lookupField: boolean;
}

export function getCustomFields(amount: number): OperatorFunction<CustomFields, CustomField[]> {
  return map<CustomFields, CustomField[]>((customisation) =>
    [...Array(amount).keys()]
      .map((x) => x + 1)
      .map(
        (x) =>
          customisation[`custom${x}`] || {
            field: `Custom ${x}`,
            visible: true
          }
      )
  );
}
