import { Links } from './links';
import { WorkbasketAccessItems } from './workbasket-access-items';

export class WorkbasketAccessItemsResource {
  constructor(
    public accessItems: Array<WorkbasketAccessItems> = [],
    public _links: Links = new Links()
  ) { }
}
