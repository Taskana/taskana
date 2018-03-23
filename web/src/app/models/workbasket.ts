import { Links } from './links';
import { ICONTYPES } from './type';
export class Workbasket {

    public static equals(org: Workbasket, comp: Workbasket): boolean {
        if (org.workbasketId !== comp.workbasketId) { return false; }
        if (org.created !== comp.created) { return false; }
        if (org.key !== comp.key) { return false; }
        if (org.domain !== comp.domain) { return false; }
        if (org.type !== comp.type) { return false; }
        if (org.modified !== comp.modified) { return false; }
        if (org.name !== comp.name) { return false; }
        if (org.description !== comp.description) { return false; }
        if (org.owner !== comp.owner) { return false; }
        if (org.custom1 !== comp.custom1) { return false; }
        if (org.custom2 !== comp.custom2) { return false; }
        if (org.custom3 !== comp.custom3) { return false; }
        if (org.custom4 !== comp.custom4) { return false; }
        if (org.orgLevel1 !== comp.orgLevel1) { return false; }
        if (org.orgLevel2 !== comp.orgLevel2) { return false; }
        if (org.orgLevel3 !== comp.orgLevel3) { return false; }
        if (org.orgLevel4 !== comp.orgLevel4) { return false; }

        return true;
    }

    constructor(
        public workbasketId: string,
        public created: string = undefined,
        public key: string = undefined,
        public domain: string = undefined,
        public type: ICONTYPES = ICONTYPES.PERSONAL,
        public modified: string = undefined,
        public name: string = undefined,
        public description: string = undefined,
        public owner: string = undefined,
        public custom1: string = undefined,
        public custom2: string = undefined,
        public custom3: string = undefined,
        public custom4: string = undefined,
        public orgLevel1: string = undefined,
        public orgLevel2: string = undefined,
        public orgLevel3: string = undefined,
        public orgLevel4: string = undefined,
        public _links: Links = new Links()) {
    }
}
