import { throwError as observableThrowError, Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketAccessItems } from 'app/shared/models/workbasket-access-items';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';
import { Direction } from 'app/shared/models/sorting';

import { DomainService } from 'app/shared/services/domain/domain.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { mergeMap, tap, catchError } from 'rxjs/operators';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { WorkbasketRepresentation } from '../../models/workbasket-representation';

@Injectable()
export class WorkbasketService {
  public workBasketSelected = new Subject<string>();
  public workBasketSaved = new Subject<number>();
  public workbasketActionToolbarExpanded = new Subject<boolean>();
  private workbasketSummaryRef: Observable<WorkbasketSummaryRepresentation> = new Observable();

  constructor(private httpClient: HttpClient, private domainService: DomainService) {}

  // #region "REST calls"
  // GET
  getWorkBasketsSummary(
    forceRequest: boolean = false,
    sortBy: string = TaskanaQueryParameters.parameters.KEY,
    order: string = Direction.ASC,
    name?: string,
    nameLike?: string,
    descLike?: string,
    owner?: string,
    ownerLike?: string,
    type?: string,
    key?: string,
    keyLike?: string,
    requiredPermission?: string,
    allPages: boolean = false
  ) {
    if (this.workbasketSummaryRef && !forceRequest) {
      return this.workbasketSummaryRef;
    }

    return this.domainService.getSelectedDomain().pipe(
      mergeMap((domain) => {
        this.workbasketSummaryRef = this.httpClient.get<WorkbasketSummaryRepresentation>(
          `${environment.taskanaRestUrl}/v1/workbaskets/${TaskanaQueryParameters.getQueryParameters(
            this.workbasketParameters(
              sortBy,
              order,
              name,
              nameLike,
              descLike,
              owner,
              ownerLike,
              type,
              key,
              keyLike,
              requiredPermission,
              allPages,
              domain
            )
          )}`
        );
        return this.workbasketSummaryRef;
      }),
      tap(() => {
        this.domainService.domainChangedComplete();
      })
    );
  }

  // GET
  getWorkBasket(id: string): Observable<Workbasket> {
    return this.httpClient.get<Workbasket>(`${environment.taskanaRestUrl}/v1/workbaskets/${id}`);
  }

  // GET
  getAllWorkBaskets(): Observable<WorkbasketRepresentation> {
    return this.httpClient.get<WorkbasketRepresentation>(
      `${environment.taskanaRestUrl}/v1/workbaskets?required-permission=OPEN`
    );
  }

  // POST
  createWorkbasket(workbasket: Workbasket): Observable<Workbasket> {
    return this.httpClient.post<Workbasket>(`${environment.taskanaRestUrl}/v1/workbaskets`, workbasket);
  }

  // PUT
  updateWorkbasket(url: string, workbasket: Workbasket): Observable<Workbasket> {
    return this.httpClient.put<Workbasket>(url, workbasket).pipe(catchError(this.handleError));
  }

  // delete
  markWorkbasketForDeletion(url: string): Observable<any> {
    return this.httpClient.delete<any>(url, { observe: 'response' });
  }

  // GET
  getWorkBasketAccessItems(url: string): Observable<WorkbasketAccessItemsRepresentation> {
    return this.httpClient.get<WorkbasketAccessItemsRepresentation>(url);
  }

  // POST
  createWorkBasketAccessItem(
    url: string,
    workbasketAccessItem: WorkbasketAccessItems
  ): Observable<WorkbasketAccessItems> {
    return this.httpClient.post<WorkbasketAccessItems>(url, workbasketAccessItem);
  }

  // PUT
  updateWorkBasketAccessItem(url: string, workbasketAccessItem: Array<WorkbasketAccessItems>): Observable<string> {
    return this.httpClient.put<string>(url, workbasketAccessItem);
  }

  // GET
  getWorkBasketsDistributionTargets(url: string): Observable<WorkbasketDistributionTargets> {
    return this.httpClient.get<WorkbasketDistributionTargets>(url);
  }

  // PUT
  updateWorkBasketsDistributionTargets(
    url: string,
    distributionTargetsIds: Array<string>
  ): Observable<WorkbasketDistributionTargets> {
    return this.httpClient.put<WorkbasketDistributionTargets>(url, distributionTargetsIds);
  }

  // DELETE
  removeDistributionTarget(url: string) {
    return this.httpClient.delete<string>(url);
  }

  // #endregion
  // #region "Service extras"
  selectWorkBasket(id?: string) {
    this.workBasketSelected.next(id);
  }

  getSelectedWorkBasket(): Observable<string> {
    return this.workBasketSelected.asObservable();
  }

  expandWorkbasketActionToolbar(value: boolean) {
    this.workbasketActionToolbarExpanded.next(value);
  }

  getWorkbasketActionToolbarExpansion(): Observable<boolean> {
    return this.workbasketActionToolbarExpanded.asObservable();
  }

  triggerWorkBasketSaved() {
    this.workBasketSaved.next(Date.now());
  }

  workbasketSavedTriggered(): Observable<number> {
    return this.workBasketSaved.asObservable();
  }

  // #endregion

  // #region private

  private handleError(error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    return observableThrowError(errMsg);
  }

  private workbasketParameters(
    sortBy: string = TaskanaQueryParameters.parameters.KEY,
    order: string = Direction.ASC,
    name?: string,
    nameLike?: string,
    descLike?: string,
    owner?: string,
    ownerLike?: string,
    type?: string,
    key?: string,
    keyLike?: string,
    requiredPermission?: string,
    allPages?: boolean,
    domain?: string
  ): QueryParameters {
    const parameters = new QueryParameters();
    parameters.SORTBY = sortBy;
    parameters.SORTDIRECTION = order;
    parameters.NAME = name;
    parameters.NAMELIKE = nameLike;
    parameters.DESCLIKE = descLike;
    parameters.OWNER = owner;
    parameters.OWNERLIKE = ownerLike;
    parameters.TYPE = type;
    parameters.KEY = key;
    parameters.KEYLIKE = keyLike;
    parameters.REQUIREDPERMISSION = requiredPermission;
    parameters.DOMAIN = domain;
    if (allPages) {
      delete TaskanaQueryParameters.page;
      delete TaskanaQueryParameters.pageSize;
    }
    return parameters;
  }

  // #endregion
}
