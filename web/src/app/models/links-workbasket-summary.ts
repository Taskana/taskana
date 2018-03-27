import { Links } from './links';

export class LinksWorkbasketSummary extends Links {
    constructor(
        self = undefined,
        distributionTargets = undefined,
        accessItems = undefined,
        public allWorkbaskets: { 'href': string } = undefined
    ) { super(self, distributionTargets, accessItems) }
}
