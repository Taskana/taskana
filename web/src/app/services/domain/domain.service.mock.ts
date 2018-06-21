import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class DomainServiceMock {


  private domainSelectedValue;
  private domainSelected = new BehaviorSubject<string>('DOMAIN_A');

  constructor() {
  }

  // GET
  getDomains(): Observable<string[]> {
    return Observable.of<string[]>([]);
  }

  getSelectedDomain(): Observable<string> {
    return this.domainSelected.asObservable();
  }

  selectDomain(value: string) {
    this.domainSelectedValue = value;
    this.domainSelected.next(value);
  }

  domainChangedComplete() {
  }

  getSelectedDomainValue() {
  }

  addMasterDomain() {
  }

  removeMasterDomain() {
  }

  switchDomain(value: string) {
    this.selectDomain(value)
  }
}
