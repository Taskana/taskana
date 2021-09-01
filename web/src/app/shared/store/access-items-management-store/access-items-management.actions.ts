import { AccessId } from '../../models/access-id';
import { Sorting, WorkbasketAccessItemQuerySortParameter } from '../../models/sorting';
import { WorkbasketAccessItemQueryFilterParameter } from '../../models/workbasket-access-item-query-filter-parameter';
import { QueryPagingParameter } from '../../models/query-paging-parameter';

export class SelectAccessId {
  static readonly type = '[Access Items Management] Select access ID';
  constructor(public accessIdDefinition: AccessId) {}
}

export class GetGroupsByAccessId {
  static readonly type = '[Access Items Management] Get groups by access ID';
  constructor(public accessId: string) {}
}

export class GetAccessItems {
  static readonly type = '[Access Items Management] Get access items';
  constructor(
    public filterParameter?: WorkbasketAccessItemQueryFilterParameter,
    public sortParameter?: Sorting<WorkbasketAccessItemQuerySortParameter>,
    public pagingParameter?: QueryPagingParameter
  ) {}
}

export class RemoveAccessItemsPermissions {
  static readonly type = "[Access Items Management] Remove access items' permissions";
  constructor(public accessId: string) {}
}
