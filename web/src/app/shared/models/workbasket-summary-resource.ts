import { Page } from 'app/shared/models/page';
import { WorkbasketSummary } from './workbasket-summary';
import { LinksWorkbasketSummary } from './links-workbasket-summary';

export class WorkbasketSummaryResource {
  constructor(
    public workbaskets: Array<WorkbasketSummary> = [],
    public _links: LinksWorkbasketSummary = new LinksWorkbasketSummary(),
    public page: Page = new Page()
  ) {
  }
}
