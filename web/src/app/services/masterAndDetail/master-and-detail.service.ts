import { Injectable } from '@angular/core';
import { Observable ,  BehaviorSubject } from 'rxjs';

@Injectable()
export class MasterAndDetailService {
  public showDetail = new BehaviorSubject<boolean>(false);


  constructor() { }

  setShowDetail(newValue: boolean) {
    this.showDetail.next(newValue);
  }

  getShowDetail() {
    return this.showDetail.asObservable();
  }

}
