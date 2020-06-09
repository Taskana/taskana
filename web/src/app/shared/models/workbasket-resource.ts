import { Links } from './links';
import { Workbasket } from './workbasket';

export interface WorkbasketResource {
  workbaskets: Workbasket[];
  _links: Links;
}
