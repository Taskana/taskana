import { HttpClient } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { Pair } from 'app/models/pair';

@Injectable()
export class ClassificationCategoriesService {

  private url = environment.taskanaRestUrl + '/v1/classification-categories';
  private dataObs$ = new ReplaySubject<Array<string>>(1);
  private categoriesObject = new Object();
  private missingIcon = 'assets/icons/categories/missing-icon.svg';

  constructor(
    private httpClient: HttpClient,
    private customFieldsService: CustomFieldsService) { }

  getCategories(forceRefresh = false): Observable<Array<string>> {
    if (!this.dataObs$.observers.length || forceRefresh) {
      this.httpClient.get<Array<string>>(this.url).subscribe(
        data => { this.dataObs$.next(data); this.categoriesObject = this.getCustomCategoriesObject(data) },
        error => {
          this.dataObs$.error(error);
          this.dataObs$ = new ReplaySubject(1);
        }
      );
    }

    return this.dataObs$;
  };

  getCategoryIcon(category: string): Pair {
    let categoryIcon = this.categoriesObject[category];
    let text = category;
    if (!categoryIcon) {
      categoryIcon = this.missingIcon;
      text = 'Category does not match with the configuration'
    }
    return new Pair(categoryIcon, text);
  }

  private getCustomCategoriesObject(categories: Array<string>): Object {
    return this.customFieldsService.getCustomObject(
      this.getDefaultCategoryMap(categories), 'classifications.categories');
  }


  private getDefaultCategoryMap(categoryList: Array<string>): Object {
    const defaultCategoryMap = new Object();
    categoryList.forEach(element => {
      defaultCategoryMap[element] = `assets/icons/categories/${element.toLowerCase()}.svg`;
    });
    return defaultCategoryMap;
  }
}
