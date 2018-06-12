import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { RequestInProgressService } from '../requestInProgress/request-in-progress.service';
import { Subject } from 'rxjs/Subject';
import { ReplaySubject } from 'rxjs/ReplaySubject';

@Injectable()
export class DomainService {

  url = environment.taskanaRestUrl + '/v1/domains';

  private domainSelectedValue;
  private domainSelected = new BehaviorSubject<string>('');
  private domainSwitched = new Subject<string>();
  private dataObs$ = new ReplaySubject<Array<string>>(1);

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private requestInProgressService: RequestInProgressService) {
  }

  // GET
  getDomains(forceRefresh = false): Observable<string[]> {
    if (forceRefresh) {
      this.httpClient.get<string[]>(this.url).subscribe(
        domains => {
          this.dataObs$.next(domains);
          if (!this.domainSelectedValue && domains && domains.length > 0) {
            this.domainSelectedValue = domains[0];
            this.selectDomain(domains[0]);
          }
        },
        error => {
          this.dataObs$.error(error);
          this.dataObs$ = new ReplaySubject(1);
        }
      );
    }

    return this.dataObs$;
  }

  getSelectedDomain(): Observable<string> {
    return this.domainSelected.asObservable();
  }

  getSwitchedDomain(): Observable<string> {
    return this.domainSwitched.asObservable();
  }

  selectDomain(value: string) {
    // this.requestInProgressService.setRequestInProgress(true);
    this.domainSelectedValue = value;
    this.domainSelected.next(value);
  }

  switchDomain(value: string) {
    this.selectDomain(value);
    this.domainSwitched.next(value);
    this.router.navigate([this.getNavigationUrl()]);
  }

  domainChangedComplete() {
    this.requestInProgressService.setRequestInProgress(false);
  }

  getSelectedDomainValue() {
    return this.domainSelectedValue;
  }

  private getNavigationUrl(): string {
    if (this.router.url.indexOf('workbaskets') !== -1) {
      return 'administration/workbaskets';
    } else if (this.router.url.indexOf('classifications') !== -1) {
      return 'administration/classifications';
    }
  }
}
