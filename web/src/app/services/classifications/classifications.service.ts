import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';
import { ClassificationDefinition } from 'app/models/classification-definition';

import { ClassificationResource } from '../../models/classification-resource';

@Injectable()
export class ClassificationsService {

  private url = environment.taskanaRestUrl + '/v1/classifications';
  private classificationSelected = new Subject<string>();
  private classificationSaved = new Subject<number>();
  private classificationTypeSelectedValue = 'TASK';
  private classificationTypeSelected = new BehaviorSubject<string>(this.classificationTypeSelectedValue);

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  private classificationRef: Observable<ClassificationResource>;
  private classificationTypes: Array<string>;

  constructor(private httpClient: HttpClient) {
  }

  // GET
  getClassifications(forceRequest = false, domain = ''): Observable<Array<TreeNodeModel>> {

    if (!forceRequest && this.classificationRef) {
      return this.classificationRef.map((response: ClassificationResource) => {
        if (!response._embedded) {
          return [];
        }
        return this.buildHierarchy(response._embedded.classificationSummaryResourceList, this.classificationTypeSelectedValue, domain);
      });
    }
    this.classificationRef = this.httpClient.get<ClassificationResource>(`${environment.taskanaRestUrl}/v1/classifications`,
      this.httpOptions);

    return this.classificationRef.map((response: ClassificationResource) => {
      if (!response._embedded) {
        return [];
      }
      return this.buildHierarchy(response._embedded.classificationSummaryResourceList, this.classificationTypeSelectedValue, domain);
    });
  }

  // GET
  getClassification(id: string): Observable<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${environment.taskanaRestUrl}/v1/classifications/${id}`, this.httpOptions)
      .do((classification: ClassificationDefinition) => {
        if (classification) {
          this.selectClassificationType(classification.type);
        }
      });
  }

  getClassificationTypes(): Observable<Array<string>> {
    const typesSubject = new Subject<Array<string>>();
    this.classificationRef.subscribe((classifications: ClassificationResource) => {
      if (!classifications._embedded) {
        return typesSubject;
      }
      const types = new Map<string, string>();
      classifications._embedded.classificationSummaryResourceList.forEach(element => {
        types.set(element.type, element.type);
      });

      typesSubject.next(this.map2Array(types));
    });
    return typesSubject.asObservable();
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

  selectClassificationType(id: string) {
    this.classificationTypeSelectedValue = id;
    this.classificationTypeSelected.next(id);
  }

  getSelectedClassificationType(): Observable<string> {
    return this.classificationTypeSelected.asObservable();
  }

  // #endregion

  private buildHierarchy(classifications: Array<Classification>, type: string, domain: string) {
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

