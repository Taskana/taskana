import { Links } from './links';

export class LinksClassification extends Links {
    constructor(
        self = undefined,
        distributionTargets = undefined,
        accessItems = undefined,
        public getAllClassifications: { 'href': string } = undefined,
        public createClassification: { 'href': string } = undefined,
        public updateClassification: { 'href': string } = undefined,
    ) { super(self, distributionTargets, accessItems) }
}
