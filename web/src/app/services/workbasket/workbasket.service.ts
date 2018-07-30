
import { throwError as observableThrowError, Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { Workbasket } from 'app/models/workbasket';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';
import { Direction } from 'app/models/sorting';

import { DomainService } from 'app/services/domain/domain.service';
import { WorkbasketResource } from '../../models/workbasket-resource';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { mergeMap, tap, catchError } from 'rxjs/operators';

@Injectable()
export class WorkbasketService {

	public workBasketSelected = new Subject<string>();
	public workBasketSaved = new Subject<number>();
	private workbasketSummaryRef: Observable<WorkbasketSummaryResource> = new Observable();

	constructor(
		private httpClient: HttpClient,
		private domainService: DomainService
	) { }

	// #region "REST calls"
	// GET
	getWorkBasketsSummary(forceRequest: boolean = false,
		sortBy: string = TaskanaQueryParameters.KEY,
		order: string = Direction.ASC,
		name: string = undefined,
		nameLike: string = undefined,
		descLike: string = undefined,
		owner: string = undefined,
		ownerLike: string = undefined,
		type: string = undefined,
		key: string = undefined,
		keyLike: string = undefined,
		requiredPermission: string = undefined,
		allPages: boolean = false) {

		if (this.workbasketSummaryRef && !forceRequest) {
			return this.workbasketSummaryRef;
		}

		return this.domainService.getSelectedDomain().pipe(mergeMap(domain => {
			return this.workbasketSummaryRef = this.httpClient.get<WorkbasketSummaryResource>(
				`${environment.taskanaRestUrl}/v1/workbaskets/${TaskanaQueryParameters.getQueryParameters(
					sortBy, order, name,
					nameLike, descLike, owner, ownerLike, type, key, keyLike, requiredPermission,
					!allPages ? TaskanaQueryParameters.page : undefined, !allPages ? TaskanaQueryParameters.pageSize : undefined, domain)}`)
				.pipe(tap((workbaskets => {
					return workbaskets;
				})))
		}), tap(() => {
			this.domainService.domainChangedComplete();
		}));

	}
	// GET
	getWorkBasket(id: string): Observable<Workbasket> {
		return this.httpClient.get<Workbasket>(`${environment.taskanaRestUrl}/v1/workbaskets/${id}`);
	}

	// GET
	getAllWorkBaskets(): Observable<WorkbasketResource> {
		return this.httpClient.get<WorkbasketResource>(`${environment.taskanaRestUrl}/v1/workbaskets?required-permission=OPEN`);
	}

	// POST
	createWorkbasket(workbasket: Workbasket): Observable<Workbasket> {
		return this.httpClient
			.post<Workbasket>(`${environment.taskanaRestUrl}/v1/workbaskets`, workbasket);

	}
	// PUT
	updateWorkbasket(url: string, workbasket: Workbasket): Observable<Workbasket> {
		return this.httpClient
			.put<Workbasket>(url, workbasket).pipe(
				catchError(this.handleError)
			);
	}
	// DELETE
	deleteWorkbasket(url: string) {
		return this.httpClient.delete(url);
	}
	// GET
	getWorkBasketAccessItems(url: string): Observable<WorkbasketAccessItemsResource> {
		return this.httpClient.get<WorkbasketAccessItemsResource>(url);
	}
	// POST
	createWorkBasketAccessItem(url: string, workbasketAccessItem: WorkbasketAccessItems): Observable<WorkbasketAccessItems> {
		return this.httpClient.post<WorkbasketAccessItems>(url, workbasketAccessItem);
	}
	// PUT
	updateWorkBasketAccessItem(url: string, workbasketAccessItem: Array<WorkbasketAccessItems>): Observable<string> {
		return this.httpClient.put<string>(url,
			workbasketAccessItem);
	}
	// GET
	getWorkBasketsDistributionTargets(url: string): Observable<WorkbasketDistributionTargetsResource> {
		return this.httpClient.get<WorkbasketDistributionTargetsResource>(url);
	}

	// PUT
	updateWorkBasketsDistributionTargets(url: string, distributionTargetsIds: Array<string>):
		Observable<WorkbasketDistributionTargetsResource> {
		return this.httpClient.put<WorkbasketDistributionTargetsResource>(url, distributionTargetsIds);
	}
	// DELETE
	removeDistributionTarget(url: string) {
		return this.httpClient.delete<string>(url);
	}


	// #endregion
	// #region "Service extras"
	selectWorkBasket(id: string) {
		this.workBasketSelected.next(id);
	}

	getSelectedWorkBasket(): Observable<string> {
		return this.workBasketSelected.asObservable();
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
		console.error(errMsg);
		return observableThrowError(errMsg);
	}


	// #endregion
}
