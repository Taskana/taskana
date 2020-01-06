import { Links } from './links';

export class LinksClassification extends Links {
  constructor(
    self?,
    distributionTargets?,
    accessItems?,
    public getAllClassifications?: { 'href': string },
    public createClassification?: { 'href': string },
    public updateClassification?: { 'href': string },
  ) { super(self, distributionTargets, accessItems) }
}
