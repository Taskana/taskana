import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { UserInfo } from 'app/shared/models/user-info';
import { Version } from 'app/shared/models/version';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class KadaiEngineService {
  currentUserInfo: UserInfo;

  constructor(private httpClient: HttpClient) {}

  // GET
  getUserInformation(): Promise<any> {
    return this.httpClient
      .get<any>(`${environment.kadaiRestUrl}/v1/current-user-info`)
      .pipe(
        map((data) => {
          this.currentUserInfo = data;
        })
      )
      .toPromise();
  }

  hasRole(roles2Find: string[]): boolean {
    if (!this.currentUserInfo || this.currentUserInfo.roles.length < 1) {
      return false;
    }
    return !!this.findRole(roles2Find);
  }

  getVersion(): Observable<Version> {
    return this.httpClient.get<Version>(`${environment.kadaiRestUrl}/v1/version`);
  }

  logout(): Observable<string> {
    return this.httpClient.post<string>(`${environment.kadaiLogoutUrl}`, '');
  }

  isCustomRoutingRulesEnabled(): Observable<boolean> {
    return this.httpClient.get<boolean>(`${environment.kadaiRestUrl}/v1/routing-rules/routing-rest-enabled`);
  }

  isHistoryProviderEnabled(): Observable<boolean> {
    return this.httpClient.get<boolean>(`${environment.kadaiRestUrl}/v1/history-provider-enabled`);
  }

  private findRole(roles2Find: string[]) {
    return this.currentUserInfo.roles.find((role) => roles2Find.some((roleLookingFor) => role === roleLookingFor));
  }
}
