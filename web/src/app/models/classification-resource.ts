import { Classification } from './classification';
import { Links } from './links';


export class ClassificationResource {
  constructor(
    public _embedded: {
      'classificationSummaryResourceList': Array<Classification>
    } = { 'classificationSummaryResourceList': [] },
    public _links: Links = new Links(),
  ) {
  }
}
