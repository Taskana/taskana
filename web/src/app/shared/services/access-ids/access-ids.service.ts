import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { AccessIdDefinition } from 'app/shared/models/access-id';
import { Observable, of } from 'rxjs';
import { AccessItemWorkbasketResource } from 'app/shared/models/access-item-workbasket-resource';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Sorting } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';

@Injectable({
  providedIn: 'root'
})
export class AccessIdsService {
  private url = `${environment.taskanaRestUrl}/v1/access-ids`;
  private accessItemsRef: Observable<AccessItemWorkbasketResource> = new Observable();
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
    sortModel: Sorting = new Sorting('workbasket-key'),
    forceRequest: boolean = false
  ): Observable<AccessItemWorkbasketResource> {
    if (forceRequest || !this.accessItemsRef) {
      this.accessItemsRef = this.httpClient.get<AccessItemWorkbasketResource>(encodeURI(
        `${environment.taskanaRestUrl}/v1/workbasket-access-items/${TaskanaQueryParameters.getQueryParameters(
          this.accessIdsParameters(sortModel,
            accessIds,
            accessIdLike,
            workbasketKeyLike)
        )}`
      ));
    }
    return this.accessItemsRef;
  }

  removeAccessItemsPermissions(accessId: string) {
    return this.httpClient
      .delete<AccessItemWorkbasketResource>(`${environment.taskanaRestUrl}/v1/workbasket-access-items/?access-id=${accessId}`);
  }

  private accessIdsParameters(
    sortModel: Sorting,
    accessIds: Array<AccessIdDefinition>,
    accessIdLike?: string,
    workbasketKeyLike?: string
  ): QueryParameters {
    const parameters = new QueryParameters();
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
