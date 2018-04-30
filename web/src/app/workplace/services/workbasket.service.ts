import { Injectable } from '@angular/core';

import { environment } from 'app/../environments/environment';
import { Observable } from 'rxjs/Observable';
import { HttpClient } from '@angular/common/http';
import {WorkbasketResource} from 'app/models/workbasket-resource';

@Injectable()
export class WorkbasketService {
  url = `${environment.taskanaRestUrl}/v1/workbaskets`;
  workbasketKey: string;
  workbasketName: string;

  constructor(private httpClient: HttpClient) {
  }

  getAllWorkBaskets(): Observable<WorkbasketResource> {
    return this.httpClient.get<WorkbasketResource>(`${this.url}?required-permission=OPEN`);
  }
}
