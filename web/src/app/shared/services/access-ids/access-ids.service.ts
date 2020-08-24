import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { AccessIdDefinition } from 'app/shared/models/access-id';
import { Observable, of } from 'rxjs';
import { AccessItemWorkbasketResource } from 'app/shared/models/access-item-workbasket-resource';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Sorting } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { StartupService } from '../startup/startup.service';

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
    accessIds: AccessIdDefinition[],
    accessIdLike?: string,
    workbasketKeyLike?: string,
    sortModel: Sorting = new Sorting('workbasket-key')
  ): Observable<AccessItemWorkbasketResource> {
    return this.httpClient.get<AccessItemWorkbasketResource>(
      encodeURI(
        `${environment.taskanaRestUrl}/v1/workbasket-access-items/${TaskanaQueryParameters.getQueryParameters(
          AccessIdsService.accessIdsParameters(sortModel, accessIds, accessIdLike, workbasketKeyLike)
        )}`
      )
    );
  }

  removeAccessItemsPermissions(accessId: string) {
    return this.httpClient.delete<AccessItemWorkbasketResource>(
      `${environment.taskanaRestUrl}/v1/workbasket-access-items/?access-id=${accessId}`
    );
  }

  private static accessIdsParameters(
    sortModel: Sorting,
    accessIds: AccessIdDefinition[],
    accessIdLike?: string,
    workbasketKeyLike?: string
  ): QueryParameters {
    // TODO extend this query for support of multiple sortbys
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
