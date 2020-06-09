<<<<<<< HEAD
// Remnant from old design, needs to be removed, type reference should instead => models/Links.ts
export class LinksWorkbasketSummary {
=======
import { Links } from './links';

export class LinksWorkbasketSummary extends Links {
>>>>>>> TSK-1215 fixed models' attributes in workbasket
  constructor(
    self?,
    distributionTargets?,
    accessItems?,
    public allWorkbaskets?: { 'href': string }
  ) {
<<<<<<< HEAD
=======
    super(self, distributionTargets, accessItems);
>>>>>>> TSK-1215 fixed models' attributes in workbasket
  }
}
