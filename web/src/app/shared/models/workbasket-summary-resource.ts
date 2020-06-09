import { Page } from 'app/shared/models/page';
import { WorkbasketSummary } from './workbasket-summary';
import { LinksWorkbasketSummary } from './links-workbasket-summary';

export interface WorkbasketSummaryResource {
  workbaskets: Array<WorkbasketSummary>;
  _links: LinksWorkbasketSummary;
  page: Page;
}
