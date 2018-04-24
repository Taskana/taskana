import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { State } from 'app/models/state';
import { WorkbasketCounter } from 'app/monitor/models/workbasket-counter';

@Injectable()
export class RestConnectorService {
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/hal+json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };
  constructor(private httpClient: HttpClient) {
  }

  getTaskStatistics(): Observable<State[]> {
    return this.httpClient.get<Array<State>>(environment.taskanaRestUrl + '/v1/monitor/countByState?states=READY,CLAIMED,COMPLETED',
      this.httpOptions)
  }


  getWorkbasketStatistics(): Observable<WorkbasketCounter> {
    return this.httpClient.get<WorkbasketCounter>(environment.taskanaRestUrl
      + '/v1/monitor/taskcountByWorkbasketDaysAndState?daysInPast=5&states=READY,CLAIMED,COMPLETED',
      this.httpOptions)
  }

  getTaskStatusReport(): Observable<Object> {
    return this.httpClient.get(environment.taskanaRestUrl + '/v1/monitor/taskStatusReport', this.httpOptions)
  }

}
