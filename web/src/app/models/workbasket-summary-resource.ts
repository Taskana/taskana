import { WorkbasketSummary } from './workbasket-summary';
import { Page } from 'app/models/page';
import { LinksWorkbasketSummary } from './links-workbasket-summary';

export class WorkbasketSummaryResource {
    constructor(
        public _embedded: {
            'workbaskets': Array<WorkbasketSummary>
        } = { 'workbaskets': [] },
        public _links: LinksWorkbasketSummary = new LinksWorkbasketSummary(),
        public page: Page = new Page()
    ) {
    }
}
