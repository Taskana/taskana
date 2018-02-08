import { Injectable } from '@angular/core';
import { RequestOptions, Headers, Http, Response } from '@angular/http';
import { WorkbasketSummary } from '../model/workbasketSummary';
import { Workbasket } from '../model/workbasket';
import { WorkbasketAuthorization } from '../model/workbasket-authorization';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class WorkbasketService {

  public workBasketSelected = new Subject<string>();

  constructor(private http: Http) { }

  getWorkBasketsSummary(): Observable<WorkbasketSummary[]> {
    return this.http.get(environment.taskanaRestUrl + "/v1/workbaskets", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  getWorkBasket(id: string): Observable<Workbasket> {
    return this.http.get(`${environment.taskanaRestUrl}/v1/workbaskets/${id}`, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  createWorkbasket(workbasket: WorkbasketSummary): Observable<WorkbasketSummary> {
    return this.http.post(environment.taskanaRestUrl + "/v1/workbaskets", workbasket, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  deleteWorkbasket(id: string) {
    return this.http.delete(environment.taskanaRestUrl + "/v1/workbaskets/" + id, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  updateWorkbasket(workbasket: WorkbasketSummary): Observable<WorkbasketSummary> {
    return this.http.put(environment.taskanaRestUrl + "/v1/workbaskets/" + workbasket.id, workbasket, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  getAllWorkBasketAuthorizations(id: String): Observable<WorkbasketAuthorization[]> {
    return this.http.get(environment.taskanaRestUrl + "/v1/workbaskets/" + id + "/authorizations", this.createAuthorizationHeader())
      .map(res => res.json());
  }

  createWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
    return this.http.post(environment.taskanaRestUrl + "/v1/workbaskets/authorizations", workbasketAuthorization, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  updateWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
    return this.http.put(environment.taskanaRestUrl + "/v1/workbaskets/authorizations/" + workbasketAuthorization.id, workbasketAuthorization, this.createAuthorizationHeader())
      .map(res => res.json());
  }

  deleteWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization) {
    return this.http.delete(environment.taskanaRestUrl + "/v1/workbaskets/authorizations/" + workbasketAuthorization.id, this.createAuthorizationHeader());
  }

  selectWorkBasket(id: string){
    this.workBasketSelected.next(id);
  }

  getSelectedWorkBasket(): Observable<string>{
    return this.workBasketSelected.asObservable();
  }

  private createAuthorizationHeader() {
    let headers: Headers = new Headers();
    headers.append("Authorization", "Basic VEVBTUxFQURfMTpURUFNTEVBRF8x");
    headers.append("content-type", "application/json");

    return new RequestOptions({ headers: headers });
  }
}
