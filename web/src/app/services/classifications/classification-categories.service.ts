import { HttpClient } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Observable ,  ReplaySubject, BehaviorSubject } from 'rxjs';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { Pair } from 'app/models/pair';

@Injectable()
export class ClassificationCategoriesService {

  private mainUrl = environment.taskanaRestUrl;

  // categories
  private urlCategories = this.mainUrl + '/v1/classification-categories';
  private param = '/?type=';
  private dataObsCategories$ = new ReplaySubject<Array<string>>(1);
  private categoriesObject = new Object();
  private missingIcon = 'assets/icons/categories/missing-icon.svg';
  private type = 'UNKNOW';

  // type
  private classificationTypeSelectedValue = 'TASK';
  private urlType = this.mainUrl + '/v1/classification-types';
  private classificationTypeSelected = new BehaviorSubject<string>(this.classificationTypeSelectedValue);
  private dataObsType$ = new ReplaySubject<Array<string>>(1);

  constructor(
    private httpClient: HttpClient,
    private customFieldsService: CustomFieldsService) { }

  getCategories(type?: string): Observable<Array<string>> {
    if (!this.dataObsCategories$.observers.length || type !== this.type) {
      this.httpClient.get<Array<string>>(type ? this.urlCategories + this.param + type : this.urlCategories).subscribe(
        data => { this.dataObsCategories$.next(data); this.categoriesObject = this.getCustomCategoriesObject(data); this.type = type; },
        error => { this.dataObsCategories$.error(error); this.dataObsCategories$ = new ReplaySubject(1); }
      );
    }
    return this.dataObsCategories$;
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

  getClassificationTypes(forceRefresh = false): Observable<Array<string>> {
    if (!this.dataObsType$.observers.length || forceRefresh) {
      this.httpClient.get<Array<string>>(this.urlType).subscribe(
        data => this.dataObsType$.next(data),
        error => {
          this.dataObsType$.error(error);
          this.dataObsType$ = new ReplaySubject(1);
        }
      );
    }
    return this.dataObsType$;
  };

    selectClassificationType(id: string) {
      this.getCategories(id);
      this.classificationTypeSelectedValue = id;
      this.classificationTypeSelected.next(id);
    }

    getSelectedClassificationType(): Observable<string> {
      return this.classificationTypeSelected.asObservable();
    }
}
