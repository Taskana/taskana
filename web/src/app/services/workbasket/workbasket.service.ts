import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { environment } from 'environments/environment';
import { Workbasket } from 'app/models/workbasket';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';
import { Direction } from 'app/models/sorting';

import { DomainService } from 'app/services/domain/domain.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import {WorkbasketResource} from '../../models/workbasket-resource';

@Injectable()
export class WorkbasketService {

  workbasketKey: string;
  workbasketName: string;

	public workBasketSelected = new Subject<string>();
	public workBasketSaved = new Subject<number>();

	// Sorting
	readonly SORTBY = 'sort-by';
	readonly ORDER = 'order';

	// Filtering
	readonly NAME = 'name';
	readonly NAMELIKE = 'name-like';
	readonly DESCLIKE = 'description-like';
	readonly OWNER = 'owner';
	readonly OWNERLIKE = 'owner-like';
	readonly TYPE = 'type';
	readonly KEY = 'key';
	readonly KEYLIKE = 'key-like';

	// Access
	readonly REQUIREDPERMISSION = 'required-permission';

	// Pagination
	readonly PAGE = 'page';
	readonly PAGESIZE = 'page-size';

	// Domain
	readonly DOMAIN = 'domain';


	page = 1;
	pageSize = 9;

	private workbasketSummaryRef: Observable<WorkbasketSummaryResource> = new Observable();

	constructor(
		private httpClient: HttpClient,
		private domainService: DomainService,
		private requestInProgressService: RequestInProgressService
	) { }

	// #region "REST calls"
	// GET
	getWorkBasketsSummary(forceRequest: boolean = false,
		sortBy: string = this.KEY,
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

		return this.domainService.getSelectedDomain().mergeMap(domain => {
			return this.workbasketSummaryRef = this.httpClient.get<WorkbasketSummaryResource>(
				`${environment.taskanaRestUrl}/v1/workbaskets/${this.getWorkbasketSummaryQueryParameters(
					sortBy, order, name,
					nameLike, descLike, owner, ownerLike, type, key, keyLike, requiredPermission,
					!allPages ? this.page : undefined, !allPages ? this.pageSize : undefined, domain)}`)
				.do(workbaskets => {
					return workbaskets;
				});
		}).do(() => {
			this.domainService.domainChangedComplete();
		});
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
			.put<Workbasket>(url, workbasket)
			.catch(this.handleError);
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
	private getWorkbasketSummaryQueryParameters(sortBy: string,
		order: string,
		name: string,
		nameLike: string,
		descLike: string,
		owner: string,
		ownerLike: string,
		type: string,
		key: string,
		keyLike: string,
		requiredPermission: string,
		page: number,
		pageSize: number,
		domain: string): string {
		let query = '?';
		query += sortBy ? `${this.SORTBY}=${sortBy}&` : '';
		query += order ? `${this.ORDER}=${order}&` : '';
		query += name ? `${this.NAME}=${name}&` : '';
		query += nameLike ? `${this.NAMELIKE}=${nameLike}&` : '';
		query += descLike ? `${this.DESCLIKE}=${descLike}&` : '';
		query += owner ? `${this.OWNER}=${owner}&` : '';
		query += ownerLike ? `${this.OWNERLIKE}=${ownerLike}&` : '';
		query += type ? `${this.TYPE}=${type}&` : '';
		query += key ? `${this.KEY}=${key}&` : '';
		query += keyLike ? `${this.KEYLIKE}=${keyLike}&` : '';
		query += requiredPermission ? `${this.REQUIREDPERMISSION}=${requiredPermission}&` : '';
		query += page ? `${this.PAGE}=${page}&` : '';
		query += pageSize ? `${this.PAGESIZE}=${pageSize}&` : '';
		query += domain ? `${this.DOMAIN}=${domain}&` : '';

		if (query.lastIndexOf('&') === query.length - 1) {
			query = query.slice(0, query.lastIndexOf('&'))
		}
		return query;
	}

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
		return Observable.throw(errMsg);
	}


	// #endregion
}
