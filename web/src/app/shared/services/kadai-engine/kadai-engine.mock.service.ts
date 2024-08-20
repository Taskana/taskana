import { Injectable } from '@angular/core';
import { of, Observable } from 'rxjs';
import { UserInfo } from 'app/shared/models/user-info';
import { Version } from '../../models/version';

@Injectable()
export class KadaiEngineServiceMock {
  currentUserInfo: UserInfo;

  constructor() {
    this.getUserInformation();
  }

  // GET
  getUserInformation(): Promise<any> {
    this.currentUserInfo = new UserInfo('userid', [''], ['admin']);
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

  getVersion(): Observable<Version> {
    const version = new Version('1.0.0');
    return of(version);
  }

  isHistoryProviderEnabled(): Observable<boolean> {
    return of(true);
  }

  private findRole(roles2Find: Array<string>) {
    return this.currentUserInfo.roles.find((role) => roles2Find.some((roleLookingFor) => role === roleLookingFor));
  }
}
