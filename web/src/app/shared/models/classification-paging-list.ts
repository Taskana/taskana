import { Links } from './links';
import { Page } from './page';
import { ClassificationSummary } from './classification-summary';

export interface ClassificationPagingList {
  classifications: ClassificationSummary[];
  _links?: Links;
  page?: Page;
}
