import { Injectable } from '@angular/core';
import { CustomField } from '../../models/customField';

@Injectable()
export class CustomFieldsService {
  private customizedFields: any = {};
  constructor() { }

  initCustomFields(language: string = 'EN', jsonFile: any) {
    this.customizedFields = jsonFile.customizedFields[language];
  }

  getCustomField(fallbacktext: string, customPath: string = undefined): CustomField {
    if (!customPath) {
      return new CustomField(true, fallbacktext)
    }
    return this.jsonPath(customPath, fallbacktext);
  }

  private jsonPath(path: string, fallbacktext: string): CustomField {
    if (!this.customizedFields) {
      return undefined;
    };
    const paths = path.split('.');
    let value = this.customizedFields;
    paths.every(element => {
      value = value[element];
      if (!value) {
        value = new CustomField(true, fallbacktext);
        return false;
      }
      return true;
    });

    return value;
  }
}
