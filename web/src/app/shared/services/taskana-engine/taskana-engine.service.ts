import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { UserInfo } from 'app/shared/models/user-info';
import { Version } from 'app/shared/models/version';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

@Injectable()
export class TaskanaEngineService {
  currentUserInfo: UserInfo;
  constructor(private httpClient: HttpClient) {}

  // GET
  getUserInformation(): Promise<any> {
    return this.httpClient
      .get<any>(`${environment.taskanaRestUrl}/v1/current-user-info`)
      .pipe(
        map((data) => {
          this.currentUserInfo = data;
        })
      )
      .toPromise();
  }

  hasRole(roles2Find: Array<string>): boolean {
    if (!this.currentUserInfo || this.currentUserInfo.roles.length < 1) {
      return false;
    }
    if (this.findRole(roles2Find)) {
      return true;
    }
    return false;
  }

  getVersion(): Observable<Version> {
    return this.httpClient.get<Version>(`${environment.taskanaRestUrl}/v1/version`);
  }

  logout(): Observable<string> {
    return this.httpClient.post<string>(`${environment.taskanaLogoutUrl}`, '');
  }

  isHistoryProviderEnabled(): Observable<boolean> {
    return this.httpClient.get<boolean>(`${environment.taskanaRestUrl}/v1/history-provider-enabled`);
  }

  isCustomRoutingRulesEnabled$ = this.httpClient
    .get<boolean>(`${environment.taskanaRestUrl}/v1/routing-rules/routing-rest-enabled`)
    .pipe(shareReplay(1));

  private findRole(roles2Find: Array<string>) {
    return this.currentUserInfo.roles.find((role) => roles2Find.some((roleLookingFor) => role === roleLookingFor));
  }
}
