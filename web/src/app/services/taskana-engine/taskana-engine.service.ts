import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { environment } from 'environments/environment';
import { UserInfoModel } from 'app/models/user-info';
import { ReplaySubject } from 'rxjs/ReplaySubject';


@Injectable()
export class TaskanaEngineService {

  private dataObs$ = new ReplaySubject<UserInfoModel>(1);

  constructor(
    private httpClient: HttpClient
  ) { }

  // GET
  getUserInformation(forceRefresh = false): Observable<UserInfoModel> {
    if (!this.dataObs$.observers.length || forceRefresh) {
      this.httpClient.get<UserInfoModel>(`${environment.taskanaRestUrl}/v1/current-user-info`).subscribe(
        data => this.dataObs$.next(data),
        error => {
          this.dataObs$.error(error);
          this.dataObs$ = new ReplaySubject(1);
        }
      );
    }
    return this.dataObs$;
  }

}
