import { ICONTYPES } from './type';
import { Page } from './page';
import { Links } from './links';

export class WorkbasketSummary {
    constructor(
        public workbasketId?: string,
        public key?: string,
        public name?: string,
        public description?: string,
        public owner?: string,
        public modified?: string,
        public domain?: string,
        public type: string = ICONTYPES.PERSONAL,
        public orgLevel1?: string,
        public orgLevel2?: string,
        public orgLevel3?: string,
        public orgLevel4?: string,
        public markedForDeletion: boolean = false,
        public _links?: Links,
        public page?: Page ) {
    }
}
