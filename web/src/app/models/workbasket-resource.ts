import { Links } from './links';
import { Workbasket } from './workbasket';

export class WorkbasketResource {
  constructor(
    public workbaskets: Array<Workbasket> = [],
    public _links: Links = new Links()
  ) { }
}
