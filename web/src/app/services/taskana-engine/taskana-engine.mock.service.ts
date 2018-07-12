import { Injectable } from '@angular/core';
import { of, Observable } from 'rxjs';
import { UserInfoModel } from 'app/models/user-info';
import { VersionModel } from '../../models/version';

@Injectable()
export class TaskanaEngineServiceMock {

  currentUserInfo: UserInfoModel;

  constructor(
  ) {
    this.getUserInformation();
  }

  // GET
  getUserInformation(): Promise<any> {
    this.currentUserInfo = new UserInfoModel('userid', [''], ['admin']);
    return of(undefined).toPromise();
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
    const version = new VersionModel('1.0.0');
    return of(version);
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
