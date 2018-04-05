import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../../environments/environment';

@Injectable()
export class DomainService {

  url = environment.taskanaRestUrl + '/v1/domains';

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  constructor(private httpClient: HttpClient) {
  }

  // GET
  getDomains(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.url, this.httpOptions);
  }
}
