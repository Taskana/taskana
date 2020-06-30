import { Page } from 'app/shared/models/page';
import { WorkbasketSummary } from './workbasket-summary';
import { Links } from './links';

export class WorkbasketSummaryResource {
  constructor(
    public workbaskets: Array<WorkbasketSummary> = [],
    public _links: Links = {},
    public page: Page = new Page()
  ) {
  }
}
