import { Links } from './links';
import { AccessItemWorkbasket } from './access-item-workbasket';

export class AccessItemWorkbasketResource {
  constructor(
    public accessItems: Array<AccessItemWorkbasket> = [],
    public _links?: Links
  ) { }
}
