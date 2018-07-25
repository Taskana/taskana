import { TestBed, inject } from '@angular/core/testing';

import { CustomFieldsService } from './custom-fields.service';

describe('CustomFieldsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CustomFieldsService]
    });
  });

  it('should be created', inject([CustomFieldsService], (service: CustomFieldsService) => {
    expect(service).toBeTruthy();
  }));

  it('should take default icon path', inject([CustomFieldsService], (service: CustomFieldsService) => {
    const categoriesData = {'DEFAULT': 'assets/icons/categories/default.svg'}
    const returnedValue = service.getCustomObject(categoriesData, undefined);
    expect(returnedValue).toBe(categoriesData);
    expect(service).toBeTruthy();
    }));

    it('should take default icon path in merge', inject([CustomFieldsService], (service: CustomFieldsService) => {
    const json = require('./taskana-customization-test.json');
    service.initCustomFields('EN', json);
    const categoriesDefault = json.EN.classifications.categories;
    const categoriesData = {
      'EXTERNAL': 'assets/icons/categories/external.svg',
      'MANUAL': 'assets/icons/categories/manual.svg',
      'AUTOMATIC': 'assets/icons/categories/automatic.svg',
      'PROCESS': 'assets/icons/categories/external.svg'
    };
    const returnedValue = service.getCustomObject(categoriesData, 'classifications.categories');
    expect(returnedValue).toEqual(categoriesDefault);
    expect(service).toBeTruthy();
    }));

    it('should take merge icon path', inject([CustomFieldsService], (service: CustomFieldsService) => {
    const json = require('./taskana-customization-test.json');
    service.initCustomFields('EN', json);
    const categoriesData = {'DEFAULT': 'assets/icons/categories/default.svg'}
    const result = {
      'AUTOMATIC': 'assets/icons/categories/automatic.svg',
      'DEFAULT': 'assets/icons/categories/default.svg',
      'EXTERNAL': 'assets/icons/categories/external.svg',
      'MANUAL': 'assets/icons/categories/manual.svg',
      'PROCESS': 'assets/icons/categories/process.svg'
    };
    const returnedValue = service.getCustomObject(categoriesData, 'classifications.categories');
    expect(returnedValue).toEqual(result);
    expect(service).toBeTruthy();
    }));
});
