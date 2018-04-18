import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { RequestInProgressService } from '../requestInProgress/request-in-progress.service';

@Injectable()
export class DomainService {

  url = environment.taskanaRestUrl + '/v1/domains';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };
  private domainSelectedValue;
  private domainSelected = new BehaviorSubject<string>('');

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private requestInProgressService: RequestInProgressService) {
  }

  // GET
  getDomains(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.url, this.httpOptions).do(domains => {
      if (!this.domainSelectedValue && domains && domains.length > 0) {
        this.domainSelectedValue = domains[0];
        this.selectDomain(domains[0]);
      }
    });
  }

  getSelectedDomain(): Observable<string> {
    return this.domainSelected.asObservable();
  }

  selectDomain(value: string) {
    this.requestInProgressService.setRequestInProgress(true);
    // this.router.navigate(['']);
    this.domainSelectedValue = value;
    this.domainSelected.next(value);
  }

  domainChangedComplete() {
    this.requestInProgressService.setRequestInProgress(false);
  }

  getSelectedDomainValue() {
    return this.domainSelectedValue;
  }
}
