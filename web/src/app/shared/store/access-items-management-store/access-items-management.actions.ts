import { AccessIdDefinition } from '../../models/access-id';
import { Sorting } from '../../models/sorting';

export class SelectAccessId {
  static readonly type = '[Access Items Management] Select access ID';
  constructor(public accessIdDefinition: AccessIdDefinition) {}
}

export class GetGroupsByAccessId {
  static readonly type = '[Access Items Management] Get Groups By Access ID';
  constructor(public accessId: string) {}
}

export class GetAccessItems {
  static readonly type = '[Access Items Management] Get Access items';
  constructor(
    public accessIds: AccessIdDefinition[],
    public accessIdLike?: string,
    public workbasketKeyLike?: string,
    public sortModel: Sorting = new Sorting('workbasket-key')
  ) {}
}
