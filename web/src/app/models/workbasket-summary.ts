import { ICONTYPES } from './type';
import { Page } from './page';
import { Links } from './links';

export class WorkbasketSummary {
    constructor(
        public workbasketId: string = undefined,
        public key: string = undefined,
        public name: string = undefined,
        public description: string = undefined,
        public owner: string = undefined,
        public modified: string = undefined,
        public domain: string = undefined,
        public type: string = ICONTYPES.PERSONAL,
        public orgLevel1: string = undefined,
        public orgLevel2: string = undefined,
        public orgLevel3: string = undefined,
        public orgLevel4: string = undefined,
        public _links: Links = undefined,
        public page: Page = undefined) {
    }
}
