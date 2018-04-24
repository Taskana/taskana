import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'environments/environment';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/observable/combineLatest';
import 'rxjs/add/operator/map'

import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';
import { ClassificationDefinition } from 'app/models/classification-definition';

import { ClassificationResource } from 'app/models/classification-resource';
import { ClassificationTypesService } from '../classification-types/classification-types.service';
import { DomainService } from 'app/services/domain/domain.service';

@Injectable()
export class ClassificationsService {

  private url = environment.taskanaRestUrl + '/v1/classifications/';
  private classificationSelected = new Subject<string>();
  private classificationSaved = new Subject<number>();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  private classificationTypes: Array<string>;

  constructor(
    private httpClient: HttpClient,
    private classificationTypeService: ClassificationTypesService,
    private domainService: DomainService) {
  }

  // GET
  getClassifications(forceRequest = false): Observable<any> {
    return this.domainService.getSelectedDomain().mergeMap(domain => {
      const classificationTypes = this.classificationTypeService.getSelectedClassificationType();
      return this.getClassificationObservable(this.httpClient.get<ClassificationResource>(
        `${environment.taskanaRestUrl}/v1/classifications/?domain=${domain}`,
        this.httpOptions));

    }).do(() => {
      this.domainService.domainChangedComplete();
    });
  }

  // GET
  getClassification(id: string): Observable<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${environment.taskanaRestUrl}/v1/classifications/${id}`, this.httpOptions)
      .do((classification: ClassificationDefinition) => {
        if (classification) {
          this.classificationTypeService.selectClassificationType(classification.type);
        }
      });
  }


  // POST
  postClassification(classification: Classification): Observable<Classification> {
    return this.httpClient.post<Classification>(`${environment.taskanaRestUrl}/v1/classifications`, classification,
      this.httpOptions);
  }

  // PUT
  putClassification(url: string, classification: Classification): Observable<Classification> {
    return this.httpClient.put<Classification>(url, classification, this.httpOptions);
  }

  // DELETE
  deleteClassification(url: string): Observable<string> {
    return this.httpClient.delete<string>(url, this.httpOptions);
  }

  // #region "Service extras"
  selectClassification(id: string) {
    this.classificationSelected.next(id);
  }

  getSelectedClassification(): Observable<string> {
    return this.classificationSelected.asObservable();

  }

  triggerClassificationSaved() {
    this.classificationSaved.next(Date.now());
  }

  classificationSavedTriggered(): Observable<number> {
    return this.classificationSaved.asObservable();
  }

  // #endregion

  private getClassificationObservable(clasisficationRef: Observable<any>): Observable<any> {
    const classificationTypes = this.classificationTypeService.getSelectedClassificationType();
    return Observable.combineLatest(
      clasisficationRef,
      classificationTypes

    ).map((data: any[]) => {
      if (!data[0]._embedded) {
        return [];
      }
      return this.buildHierarchy(data[0]._embedded.classificationSummaryResourceList, data[1]);
    })

  }

  private buildHierarchy(classifications: Array<Classification>, type: string) {
    const roots = []
    const children = new Array<any>();

    for (let index = 0, len = classifications.length; index < len; ++index) {
      const item = classifications[index];
      if (item.type === type) {
        const parent = item.parentId,
          target = !parent ? roots : (children[parent] || (children[parent] = []));

        target.push(item);
      }
    }
    for (let index = 0, len = roots.length; index < len; ++index) {
      this.findChildren(roots[index], children);
    }
    return roots;
  }


  private findChildren(parent: any, children: Array<any>) {
    if (children[parent.classificationId]) {
      parent.children = children[parent.classificationId];
      for (let index = 0, len = parent.children.length; index < len; ++index) {
        this.findChildren(parent.children[index], children);
      }
    }
  }

  private map2Array(map: Map<string, string>): Array<string> {
    const returnArray = [];

    map.forEach((entryVal, entryKey) => {
      returnArray.push(entryKey);
    });

    return returnArray;
  }

}

