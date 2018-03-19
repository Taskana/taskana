import { Links } from './links';
import { WorkbasketAccessItems } from './workbasket-access-items';

export class WorkbasketAccessItemsResource {
    constructor(
        public _embedded: { 'accessItems': Array<WorkbasketAccessItems> } = { 'accessItems': [] },
        public _links: Links = undefined
    ) { }
}
