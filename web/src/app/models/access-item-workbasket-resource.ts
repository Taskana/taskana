import { Links } from './links';
import { AccessItemWorkbasket } from './access-item-workbasket';

export class AccessItemsWorkbasketResource {
  constructor(
    public accessItems: Array<AccessItemWorkbasket> = [],
    public _links?: Links
  ) { }
}
