export class Links {
    constructor(
        public self: { 'href': string } = undefined,
        public distributionTargets: { 'href': string } = undefined,
        public accessItems: { 'href': string } = undefined,
        public allWorkbasketUrl: { 'href': string } = undefined,
        public removeDistributionTargets: {'href': string} = undefined
    ) { }
}
