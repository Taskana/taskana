import {Links} from './links';
import {Workbasket} from './workbasket';

export class WorkbasketResource {
  constructor(public _embedded: { 'workbaskets': Array<Workbasket> } = { 'workbaskets': [] },
              public _links: Links = undefined) {}
}
