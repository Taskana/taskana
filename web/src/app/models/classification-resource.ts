import { Classification } from './classification';
import { Links } from './links';

export class ClassificationResource {
  constructor(
    public classifications: Array<Classification> = [],
    public _links: Links = new Links(),
  ) {
  }
}
