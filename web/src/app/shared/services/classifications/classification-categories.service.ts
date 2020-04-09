import { HttpClient } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import set from 'set-value';
import { Customisation } from '../../../models/customisation';

const customisationUrl = 'environments/data-sources/taskana-customization.json';

export const missingIcon = 'assets/icons/categories/missing-icon.svg';

export interface CategoriesResponse { [key: string]: string[] }

@Injectable()
export class ClassificationCategoriesService {
  private mainUrl = environment.taskanaRestUrl;
  private urlCategoriesByType = `${this.mainUrl}/v1/classifications-by-type`;

  constructor(private httpClient: HttpClient) {}

  // TODO: convert to Map (maybe via ES6)
  getClassificationCategoriesByType(): Observable<CategoriesResponse> {
    return this.httpClient.get<CategoriesResponse>(this.urlCategoriesByType);
  }

  getCustomisation(): Observable<Customisation> {
    return this.httpClient.get<Customisation>(customisationUrl).pipe(
      map(customisation => {
        Object.keys(customisation).forEach(lang => {
          set(customisation[lang], 'classifications.categories.missing', missingIcon);
        });
        return customisation;
      })
    );
  }
}
