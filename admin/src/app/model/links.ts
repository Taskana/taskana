export class Links {
    constructor(
        public self: { 'href': string },
        public distributionTargets: { 'href': string } = undefined,
        public accessItems: { 'href': string } = undefined
    ) { }
}
