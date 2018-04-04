import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

import { Classification } from 'app/models/classification';
import { TreeNode } from 'app/models/tree-node';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class ClassificationsService {

  url = environment.taskanaRestUrl + '/v1/classifications';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  private classificationRef: Observable<Array<Classification>>;
  private classificationTypes: Array<string>;

  constructor(private httpClient: HttpClient) {
  }

  // GET
  getClassifications(forceRequest = false, type = 'TASK', domain = ''): Observable<Array<TreeNode>> {
    if (!forceRequest && this.classificationRef) {
      return this.classificationRef.map((response: Array<Classification>) => {
        return this.buildHierarchy(response, type, domain);
      });
    }
    this.classificationRef = this.httpClient.get<Array<Classification>>(`${environment.taskanaRestUrl}/v1/classifications`,
      this.httpOptions);

    return this.classificationRef.map((response: Array<Classification>) => {
      return this.buildHierarchy(response, type, domain);
    });
  }

  getClassificationTypes(): Observable<Map<string, string>> {
    const typesSubject = new Subject<Map<string, string>>();
    this.classificationRef.subscribe((classifications: Array<Classification>) => {
      const types = new Map<string, string>();
      classifications.forEach(element => {
        types.set(element.type, element.type);
      });
      typesSubject.next(types);
    });
    return typesSubject.asObservable();
  }

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
    if (children[parent.id]) {
      parent.children = children[parent.id];
      for (let index = 0, len = parent.children.length; index < len; ++index) {
        this.findChildren(parent.children[index], children);
      }
    }
  }
}

