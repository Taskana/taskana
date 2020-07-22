// Remnant from old design, needs to be removed, type reference should instead => models/Links.ts
export class LinksWorkbasketSummary {
  constructor(self?, distributionTargets?, accessItems?, public allWorkbaskets?: { href: string }) {}
}
