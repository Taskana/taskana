import { Classification } from './classification';
import { Links } from './links';

export class ClassificationResource {
  constructor(
    public classifications: Classification[] = [],
    public _links?: Links
  ) {
  }
}
