import {Injectable} from '@angular/core';

import {Workbasket} from 'app/models/workbasket';
import {environment} from 'app/../environments/environment';
import {Observable} from 'rxjs/Observable';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable()
export class WorkbasketService {
  url = environment.taskanaRestUrl + '/v1/workbaskets';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/hal+json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient) {
  }

  getAllWorkBaskets(): Observable<Workbasket[]> {
    return this.httpClient.get<Workbasket[]>(this.url + '?required-permission=OPEN', this.httpOptions);
  }
}
