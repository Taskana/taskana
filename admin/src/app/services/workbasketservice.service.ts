import { Injectable } from '@angular/core';
import { HttpClientModule, HttpClient, HttpHeaders,   } from '@angular/common/http';
import { WorkbasketSummary } from '../model/workbasketSummary';
import { Workbasket } from '../model/workbasket';
import { WorkbasketAuthorization } from '../model/workbasket-authorization';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';


//sort direction
export enum Direction{
  ASC = "asc",
  DESC = "desc"
};

@Injectable()
export class WorkbasketService {

  public workBasketSelected = new Subject<string>();

  constructor(private httpClient: HttpClient) { }

  //Sorting
  readonly SORTBY="sortBy";
  readonly ORDER="order";

  //Filtering
  readonly NAME="name";
  readonly NAMELIKE="nameLike";
  readonly DESCLIKE="descLike";
  readonly OWNER="owner";
  readonly OWNERLIKE="ownerLike";
  readonly TYPE="type";
  readonly KEY="key";

  //Access
  readonly REQUIREDPERMISSION="requiredPermission";

  httpOptions = { 
    headers: new HttpHeaders({
      'Content-Type':  'application/json',
      'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
    })
  };

  //REST calls
  getWorkBasketsSummary(sortBy: string = this.KEY,
                        order: string = Direction.ASC,
                        name: string = undefined,
                        nameLike: string = undefined,
                        descLike: string = undefined,
                        owner:string = undefined,
                        ownerLike:string = undefined,
                        type:string = undefined,
                        requiredPermission: string = undefined): Observable<WorkbasketSummary[]> {

    return this.httpClient.get<WorkbasketSummary[]>(`${environment.taskanaRestUrl}/v1/workbaskets/${this.getWorkbasketSummaryQueryParameters(sortBy, order, name,
                          nameLike, descLike, owner, ownerLike, type, requiredPermission)}`,this.httpOptions)
                          
  }

  getWorkBasket(id: string): Observable<Workbasket> {
    return this.httpClient.get<Workbasket>(`${environment.taskanaRestUrl}/v1/workbaskets/${id}`, this.httpOptions);
  }

  createWorkbasket(workbasket: WorkbasketSummary): Observable<WorkbasketSummary> {
    return this.httpClient.post<WorkbasketSummary>(environment.taskanaRestUrl + "/v1/workbaskets", workbasket, this.httpOptions);
  }

  deleteWorkbasket(id: string) {
    return this.httpClient.delete(environment.taskanaRestUrl + "/v1/workbaskets/" + id, this.httpOptions);
  }

  updateWorkbasket(workbasket: WorkbasketSummary): Observable<WorkbasketSummary> {
    return this.httpClient.put<WorkbasketSummary>(environment.taskanaRestUrl + "/v1/workbaskets/" + workbasket.workbasketId, workbasket, this.httpOptions);
  }

  getAllWorkBasketAuthorizations(id: String): Observable<WorkbasketAuthorization[]> {
    return this.httpClient.get<WorkbasketAuthorization[]>(environment.taskanaRestUrl + "/v1/workbaskets/" + id + "/authorizations", this.httpOptions);
  }

  createWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
    return this.httpClient.post<WorkbasketAuthorization>(environment.taskanaRestUrl + "/v1/workbaskets/authorizations", workbasketAuthorization, this.httpOptions);
  }

  updateWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
    return this.httpClient.put<WorkbasketAuthorization>(environment.taskanaRestUrl + "/v1/workbaskets/authorizations/" + workbasketAuthorization.id, workbasketAuthorization, this.httpOptions)
  }

  deleteWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization) {
    return this.httpClient.delete(environment.taskanaRestUrl + "/v1/workbaskets/authorizations/" + workbasketAuthorization.id, this.httpOptions);
  }

  //Service extras
  selectWorkBasket(id: string){
    this.workBasketSelected.next(id);
  }

  getSelectedWorkBasket(): Observable<string>{
    return this.workBasketSelected.asObservable();
  }

  private getWorkbasketSummaryQueryParameters( sortBy: string,
                                        order: string,
                                        name: string,
                                        nameLike: string,
                                        descLike: string,
                                        owner:string,
                                        ownerLike:string,
                                        type:string,
                                        requiredPermission: string): string{
    let query: string = "?";
    query += sortBy?              `${this.SORTBY}=${sortBy}&`:'';
    query += order?               `${this.ORDER}=${order}&`:'';
    query += name?                `${this.NAME}=${name}&`:'';
    query += nameLike?            `${this.NAMELIKE}=${nameLike}&`:'';
    query += descLike?            `${this.DESCLIKE}=${descLike}&`:'';
    query += owner?               `${this.OWNER}=${owner}&`:'';
    query += ownerLike?           `${this.OWNERLIKE}=${ownerLike}&`:'';
    query += type?                `${this.TYPE}=${type}&`:'';
    query += requiredPermission?  `${this.REQUIREDPERMISSION}=${requiredPermission}&`:'';

    if(query.lastIndexOf('&') === query.length-1){
      query = query.slice(0, query.lastIndexOf('&'))
    }
    return query;
  }
}
