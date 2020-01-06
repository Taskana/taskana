import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { AccessIdDefinition } from 'app/models/access-id';
import { Observable, of } from 'rxjs';
import { AccessItemsWorkbasketResource } from 'app/models/access-item-workbasket-resource';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { SortingModel } from 'app/models/sorting';
import { QueryParametersModel } from 'app/models/query-parameters';

@Injectable({
  providedIn: 'root'
})
export class AccessIdsService {

  private url = `${environment.taskanaRestUrl}/v1/access-ids`;
  private accessItemsRef: Observable<AccessItemsWorkbasketResource> = new Observable();
  constructor(
    private httpClient: HttpClient
  ) { }

  getAccessItemsInformation(token: string, searchInGroups = false): Observable<Array<AccessIdDefinition>> {
    if (!token || token.length < 3) {
      return of([]);
    }
    if (searchInGroups) {
      return this.httpClient.get<Array<AccessIdDefinition>>(`${this.url}/groups?access-id=${token}`);
    }
    return this.httpClient.get<Array<AccessIdDefinition>>(`${this.url}?search-for=${token}`);

  }

  getAccessItemsPermissions(
    accessIds: Array<AccessIdDefinition>,
    accessIdLike?: string,
    workbasketKeyLike?: string,
    sortModel: SortingModel = new SortingModel('workbasket-key'),
    forceRequest: boolean = false
  ): Observable<AccessItemsWorkbasketResource> {

    if (this.accessItemsRef && !forceRequest) {
      return this.accessItemsRef;
    }

    return this.accessItemsRef = this.httpClient.get<AccessItemsWorkbasketResource>(encodeURI(
      `${environment.taskanaRestUrl}/v1/workbasket-access-items/${TaskanaQueryParameters.getQueryParameters(
        this.accessIdsParameters(sortModel,
          accessIds,
          accessIdLike, workbasketKeyLike)
      )}`
    ))
  }

  removeAccessItemsPermissions(accessId: string) {
    return this.httpClient
      .delete<AccessItemsWorkbasketResource>(`${environment.taskanaRestUrl}/v1/workbasket-access-items/?access-id=${accessId}`)
  }

  private accessIdsParameters(
    sortModel: SortingModel,
    accessIds: Array<AccessIdDefinition>,
    accessIdLike?: string,
    workbasketKeyLike?: string
  ): QueryParametersModel {

    const parameters = new QueryParametersModel();
    parameters.SORTBY = sortModel.sortBy;
    parameters.SORTDIRECTION = sortModel.sortDirection;
    parameters.ACCESSIDS = accessIds.map((values: AccessIdDefinition) => values.accessId).join('|');
    parameters.ACCESSIDLIKE = accessIdLike;
    parameters.WORKBASKETKEYLIKE = workbasketKeyLike;
    delete TaskanaQueryParameters.page;
    delete TaskanaQueryParameters.pageSize;
    return parameters;
  }
}
