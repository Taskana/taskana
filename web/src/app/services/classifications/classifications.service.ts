import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';
import { ClassificationDefinition } from 'app/models/classification-definition';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { ClassificationResource } from '../../models/classification-resource';

@Injectable()
export class ClassificationsService {

  url = environment.taskanaRestUrl + '/v1/classifications';
  classificationSelected = new Subject<string>();

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
  getClassifications(forceRequest = false, type = 'TASK', domain = ''): Observable<Array<TreeNodeModel>> {
    if (!forceRequest && this.classificationRef) {
      return this.classificationRef.map((response: ClassificationResource) => {
        if (!response._embedded) {
          return [];
        }
        return this.buildHierarchy(response._embedded.classificationSummaryResourceList, type, domain);
      });
    }
    this.classificationRef = this.httpClient.get<ClassificationResource>(`${environment.taskanaRestUrl}/v1/classifications`,
      this.httpOptions);

    return this.classificationRef.map((response: ClassificationResource) => {
      if (!response._embedded) {
        return [];
      }
      return this.buildHierarchy(response._embedded.classificationSummaryResourceList, type, domain);
    });
  }

  // GET
  getClassification(id: string): Observable<ClassificationDefinition> {
    return this.httpClient.get<ClassificationDefinition>(`${environment.taskanaRestUrl}/v1/classifications/${id}`, this.httpOptions);
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

  // #region "Service extras"
  selectClassification(id: string) {
    this.classificationSelected.next(id);
  }

  getSelectedClassification(): Observable<string> {
    return this.classificationSelected.asObservable();
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

