import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export class WorkbasketDistributionTargetsResource {
    constructor(public _embedded: { 'distributionTargets': Array<WorkbasketSummary> } =
        { 'distributionTargets': [] }, public _links: Links = null) {
    }
}
