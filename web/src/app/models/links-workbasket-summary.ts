import { Links } from './links';

export class LinksWorkbasketSummary extends Links {
  constructor(
    self?,
    distributionTargets?,
    accessItems?,
    public allWorkbaskets?: { 'href': string }
  ) { super(self, distributionTargets, accessItems) }
}
