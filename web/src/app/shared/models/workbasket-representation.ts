import { Links } from './links';
import { Workbasket } from './workbasket';

export interface WorkbasketRepresentation {
  workbaskets: Workbasket[];
  _links: Links;
}
