import { AccessIdDefinition } from '../../models/access-id';
import { Sorting } from '../../models/sorting';

export class SelectAccessId {
  static readonly type = '[Access Items Management] Select access ID';
  constructor(public accessIdDefinition: AccessIdDefinition) {}
}

export class GetGroupsByAccessId {
  static readonly type = '[Access Items Management] Get groups by access ID';
  constructor(public accessId: string) {}
}

export class GetAccessItems {
  static readonly type = '[Access Items Management] Get access items';
  constructor(
    public accessIds: AccessIdDefinition[],
    public accessIdLike?: string,
    public workbasketKeyLike?: string,
    public sortModel: Sorting = new Sorting('workbasket-key')
  ) {}
}

export class RemoveAccessItemsPermissions {
  static readonly type = "[Access Items Management] Remove access items' permissions";
  constructor(public accessId: string) {}
}
