import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { AccessIdDefinition } from 'app/models/access-id';
import { Observable, of } from 'rxjs';
import { AccessItemsWorkbasketResource } from 'app/models/access-item-workbasket-resource';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { SortingModel } from 'app/models/sorting';

@Injectable({
  providedIn: 'root'
})
export class AccessIdsService {

  private url = environment.taskanaRestUrl + '/v1/access-ids';
  private accessItemsRef: Observable<AccessItemsWorkbasketResource> = new Observable();
  constructor(
    private httpClient: HttpClient) { }

  getAccessItemsInformation(token: string, searchInGroups = false): Observable<Array<AccessIdDefinition>> {
    if (!token || token.length < 3) {
      return of([]);
    }
    return this.httpClient.get<Array<AccessIdDefinition>>(`${this.url}?searchFor=${token}&searchInGroups=${searchInGroups}`);
  };

  getAccessItemsPermissions(
    accessIds: Array<AccessIdDefinition>,
    accessIdLike: string = undefined,
    workbasketKeyLike: string = undefined,
    sortModel: SortingModel = new SortingModel('workbasket-key'),
    forceRequest: boolean = false): Observable<AccessItemsWorkbasketResource> {

    if (this.accessItemsRef && !forceRequest) {
      return this.accessItemsRef;
    }

    return this.accessItemsRef = this.httpClient.get<AccessItemsWorkbasketResource>(encodeURI(
      `${environment.taskanaRestUrl}/v1/workbasket-access/${TaskanaQueryParameters.getQueryParameters(sortModel.sortBy,
    sortModel.sortDirection,
        undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        accessIds.map((values: AccessIdDefinition) => {
          return values.accessId
        }).join('|'),
        accessIdLike, workbasketKeyLike)}`))
  }
}
