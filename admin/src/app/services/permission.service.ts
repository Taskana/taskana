import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class PermissionService {

  private permission = new BehaviorSubject<boolean>(true);


  setPermission(permission: boolean) {
    this.permission.next(permission);
  }

  hasPermission(): Observable<boolean> {
    return this.permission.asObservable();
  }
}