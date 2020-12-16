import { throwError as observableThrowError, Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkbasketAccessItems } from 'app/shared/models/workbasket-access-items';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { WorkbasketDistributionTargets } from 'app/shared/models/workbasket-distribution-targets';
import { Sorting, WorkbasketQuerySortParameter } from 'app/shared/models/sorting';

import { DomainService } from 'app/shared/services/domain/domain.service';
import { mergeMap, tap, catchError } from 'rxjs/operators';
import { WorkbasketRepresentation } from '../../models/workbasket-representation';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { QueryPagingParameter } from '../../models/query-paging-parameter';
import { asUrlQueryString } from '../../util/query-parameters-v2';

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
    filterParameter?: WorkbasketQueryFilterParameter,
    sortParameter?: Sorting<WorkbasketQuerySortParameter>,
    pagingParameter?: QueryPagingParameter
  ) {
    if (this.workbasketSummaryRef && !forceRequest) {
      return this.workbasketSummaryRef;
    }

    return this.domainService.getSelectedDomain().pipe(
      mergeMap((domain) => {
        this.workbasketSummaryRef = this.httpClient.get<WorkbasketSummaryRepresentation>(
          `${environment.taskanaRestUrl}/v1/workbaskets/${asUrlQueryString({
            ...filterParameter,
            ...sortParameter,
            ...pagingParameter
          })}`
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

  // #endregion
}
