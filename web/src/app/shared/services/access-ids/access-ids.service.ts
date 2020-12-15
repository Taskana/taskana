import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { AccessIdDefinition } from 'app/shared/models/access-id';
import { Observable, of } from 'rxjs';
import { WorkbasketAccessItemsRepresentation } from 'app/shared/models/workbasket-access-items-representation';
import { Sorting, WorkbasketAccessItemQuerySortParameter } from 'app/shared/models/sorting';
import { StartupService } from '../startup/startup.service';
import { WorkbasketAccessItemQueryFilterParameter } from '../../models/workbasket-access-item-query-filter-parameter';
import { QueryPagingParameter } from '../../models/query-paging-parameter';
import { asUrlQueryString } from '../../util/query-parameters-v2';

@Injectable({
  providedIn: 'root'
})
export class AccessIdsService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/access-ids';
  }

  searchForAccessId(accessId: string): Observable<AccessIdDefinition[]> {
    if (!accessId || accessId.length < 3) {
      return of([]);
    }
    return this.httpClient.get<AccessIdDefinition[]>(`${this.url}?search-for=${accessId}`);
  }

  getGroupsByAccessId(accessId: string): Observable<AccessIdDefinition[]> {
    if (!accessId || accessId.length < 3) {
      return of([]);
    }
    return this.httpClient.get<AccessIdDefinition[]>(`${this.url}/groups?access-id=${accessId}`);
  }

  getAccessItems(
    filterParameter?: WorkbasketAccessItemQueryFilterParameter,
    sortParameter?: Sorting<WorkbasketAccessItemQuerySortParameter>,
    pagingParameter?: QueryPagingParameter
  ): Observable<WorkbasketAccessItemsRepresentation> {
    return this.httpClient.get<WorkbasketAccessItemsRepresentation>(
      encodeURI(
        `${environment.taskanaRestUrl}/v1/workbasket-access-items/${asUrlQueryString({
          ...filterParameter,
          ...sortParameter,
          ...pagingParameter
        })}`
      )
    );
  }

  removeAccessItemsPermissions(accessId: string) {
    return this.httpClient.delete<WorkbasketAccessItemsRepresentation>(
      `${environment.taskanaRestUrl}/v1/workbasket-access-items/?access-id=${accessId}`
    );
  }
}
