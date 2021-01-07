import { HttpClient } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Customisation } from '../../models/customisation';

const customisationUrl = 'environments/data-sources/taskana-customization.json';

export const missingIcon = 'assets/icons/categories/missing-icon.svg';
export const asteriskIcon = './assets/icons/asterisk.svg';

export interface CategoriesResponse {
  [key: string]: string[];
}

@Injectable()
export class ClassificationCategoriesService {
  constructor(private httpClient: HttpClient) {}

  // TODO: convert to Map (maybe via ES6)
  getClassificationCategoriesByType(): Observable<CategoriesResponse> {
    return this.httpClient.get<CategoriesResponse>(`${environment.taskanaRestUrl}/v1/classifications-by-type`);
  }

  getCustomisation(): Observable<Customisation> {
    return this.httpClient.get<Customisation>(customisationUrl).pipe(
      map((customisation) => {
        Object.keys(customisation).forEach((lang) => {
          if (customisation[lang]?.classifications?.categories) {
            customisation[lang].classifications.categories.missing = missingIcon;
            customisation[lang].classifications.categories.all = asteriskIcon;
          } else {
            if (customisation[lang]?.classifications) {
              customisation[lang].classifications.categories = { missing: missingIcon, all: asteriskIcon };
            } else {
              customisation[lang].classifications = { categories: { missing: missingIcon, all: asteriskIcon } };
            }
          }
        });
        return customisation;
      })
    );
  }
}
