export class Links {
  constructor(
    public self?: { 'href': string },
    public distributionTargets?: { 'href': string },
    public accessItems?: { 'href': string },
    public allWorkbasketUrl?: { 'href': string },
    public removeDistributionTargets?: {'href': string}
  ) { }
}
