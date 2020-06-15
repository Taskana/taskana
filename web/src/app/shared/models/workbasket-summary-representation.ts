import { Page } from 'app/shared/models/page';
import { WorkbasketSummary } from './workbasket-summary';
import { LinksWorkbasketSummary } from './links-workbasket-summary';

export interface WorkbasketSummaryRepresentation {
  workbaskets: WorkbasketSummary[];
  _links: LinksWorkbasketSummary;
<<<<<<< HEAD
  page?: Page;
=======
  page: Page;
>>>>>>> TSK-1215 correct the naming scheme for models in workbasket
}
