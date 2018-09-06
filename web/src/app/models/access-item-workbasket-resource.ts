import { Links } from './links';
import { AccessItemWorkbasket } from './access-item-workbasket';

export class AccessItemsWorkbasketResource {
    constructor(
        public _embedded: { 'accessItems': Array<AccessItemWorkbasket> } = { 'accessItems': [] },
        public _links: Links = undefined
    ) { }
}
