import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';


import { AccessIdDefinition } from 'app/models/access-id';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class AccessIdsService {

  private url = environment.taskanaRestUrl + '/v1/validate-access-id';

  constructor(
    private httpClient: HttpClient) { }

  getAccessItemsInformation(token): Observable<Array<AccessIdDefinition>> {
    if (!token) {
      return Observable.of([]);
    }
    return this.httpClient.get<Array<AccessIdDefinition>>(`${this.url}?search=${token}`);
  };

}
