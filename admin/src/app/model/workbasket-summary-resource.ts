import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export class WorkbasketSummaryResource {
    constructor(public _embedded: { 'workbaskets': Array<WorkbasketSummary> } =
        { 'workbaskets': [] }, public _links: Links = null) {
    }
}
