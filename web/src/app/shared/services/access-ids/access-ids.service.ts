import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';


import { AccessIdDefinition } from 'app/models/access-id';
import { Observable, of } from 'rxjs';

@Injectable()
export class AccessIdsService {

  private url = environment.taskanaRestUrl + '/v1/access-ids';

  constructor(
    private httpClient: HttpClient) { }

  getAccessItemsInformation(token: string): Observable<Array<AccessIdDefinition>> {
    if (!token || token.length < 3) {
      return of([]);
    }
    return this.httpClient.get<Array<AccessIdDefinition>>(`${this.url}?searchFor=${token}`);
  };

}
