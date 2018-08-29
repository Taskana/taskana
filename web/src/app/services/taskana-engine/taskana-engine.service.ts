import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { UserInfoModel } from 'app/models/user-info';
import { VersionModel } from 'app/models/version';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';


@Injectable()
export class TaskanaEngineService {

  currentUserInfo: UserInfoModel;

  constructor(
    private httpClient: HttpClient
  ) { }

  // GET
  getUserInformation(): Promise<any> {
    return this.httpClient.get<any>(`${environment.taskanaRestUrl}/v1/current-user-info`).pipe(map(
      data => {
        this.currentUserInfo = data
      })).toPromise();
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

  getVersion(): Observable<VersionModel> {
    return this.httpClient.get<VersionModel>(`${environment.taskanaRestUrl}/v1/version`);
  }

  logout(): Observable<string> {
    return this.httpClient
      .post<string>(`${environment.taskanaRestUrl}/logout`, '');

  }

  private findRole(roles2Find: Array<string>) {
    return this.currentUserInfo.roles.find(role => {
      return roles2Find.some(roleLookingFor => {
        if (role === roleLookingFor) {
          return true;
        }
      });
    });
  }
}
