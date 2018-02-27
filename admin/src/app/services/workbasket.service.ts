import { Injectable } from '@angular/core';
import { HttpClientModule, HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { WorkbasketSummary } from '../model/workbasketSummary';
import { Workbasket } from '../model/workbasket';
import { WorkbasketAuthorization } from '../model/workbasket-authorization';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { Timestamp } from 'rxjs';


//sort direction
export enum Direction {
	ASC = 'asc',
	DESC = 'desc'
};

@Injectable()
export class WorkbasketService {

	public workBasketSelected = new Subject<string>();
	public workBasketSaved = new Subject<number>();

	constructor(private httpClient: HttpClient) { }

	//Sorting
	readonly SORTBY = 'sortBy';
	readonly ORDER = 'order';

	//Filtering
	readonly NAME = 'name';
	readonly NAMELIKE = 'nameLike';
	readonly DESCLIKE = 'descLike';
	readonly OWNER = 'owner';
	readonly OWNERLIKE = 'ownerLike';
	readonly TYPE = 'type';
	readonly KEY = 'key';
	readonly KEYLIKE = 'keyLike';

	//Access
	readonly REQUIREDPERMISSION = 'requiredPermission';

	httpOptions = {
		headers: new HttpHeaders({
			'Content-Type': 'application/json',
			'Authorization': 'Basic VEVBTUxFQURfMTpURUFNTEVBRF8x'
		})
	};

	private workbasketSummaryRef: Observable<WorkbasketSummary[]>;

	//#region "REST calls"
	// GET
	getWorkBasketsSummary(	forceRequest: boolean = false,
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
							requiredPermission: string = undefined): Observable<WorkbasketSummary[]> {
		if(this.workbasketSummaryRef && !forceRequest){
			return this.workbasketSummaryRef;
		}
		return this.httpClient.get<WorkbasketSummary[]>(`${environment.taskanaRestUrl}/v1/workbaskets/${this.getWorkbasketSummaryQueryParameters(sortBy, order, name,
			nameLike, descLike, owner, ownerLike, type, key, keyLike, requiredPermission)}`, this.httpOptions);

	}

	// GET
	getWorkBasket(url: string): Observable<Workbasket> {
		return this.httpClient.get<Workbasket>(url, this.httpOptions);
	}
	
	getWorkBasket1(id: string): Observable<Workbasket> {
		return this.httpClient.get<Workbasket>(environment.taskanaRestUrl + '/v1/workbaskets/' + id, this.httpOptions);
	}
	// POST
	createWorkbasket(url: string, workbasket: Workbasket): Observable<Workbasket> {
		return this.httpClient
		.post<Workbasket>(url, this.httpOptions);
	}
	// PUT
	updateWorkbasket(url: string, workbasket: Workbasket): Observable<Workbasket> {
		return this.httpClient
			.put<Workbasket>(url, workbasket, this.httpOptions)
			.catch(this.handleError);
	}
	// DELETE
	deleteWorkbasket(id: string) {
		return this.httpClient.delete(environment.taskanaRestUrl + '/v1/workbaskets/' + id, this.httpOptions);
	}
	// GET
	getWorkBasketAuthorizations(id: String): Observable<WorkbasketAuthorization[]> {
		return this.httpClient.get<WorkbasketAuthorization[]>(environment.taskanaRestUrl + '/v1/workbaskets/' + id + '/authorizations', this.httpOptions);
	}
	// POST
	createWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
		return this.httpClient.post<WorkbasketAuthorization>(environment.taskanaRestUrl + '/v1/workbaskets/authorizations', workbasketAuthorization, this.httpOptions);
	}
	// PUT
	updateWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization): Observable<WorkbasketAuthorization> {
		return this.httpClient.put<WorkbasketAuthorization>(environment.taskanaRestUrl + '/v1/workbaskets/authorizations/' + workbasketAuthorization.accessId, workbasketAuthorization, this.httpOptions)
	}
	// DELETE
	deleteWorkBasketAuthorization(workbasketAuthorization: WorkbasketAuthorization) {
		return this.httpClient.delete(environment.taskanaRestUrl + '/v1/workbaskets/authorizations/' + workbasketAuthorization.accessId, this.httpOptions);
	}

	//#endregion 

	//#region "Service extras"
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
	//#endregion

	//#region private
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
		requiredPermission: string): string {
		let query: string = '?';
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

		if (query.lastIndexOf('&') === query.length - 1) {
			query = query.slice(0, query.lastIndexOf('&'))
		}
		return query;
	}

	private handleError (error: Response | any) {
		// In a real world app, you might use a remote logging infrastructure
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

	//#endregion 
}
