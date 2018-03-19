import { Links } from './links';

export class WorkbasketAccessItems {
    constructor(
        public accessItemId: string = '',
        public workbasketId: string = '',
        public accessId: string = '',
        public permRead: boolean = false,
        public permOpen: boolean = false,
        public permAppend: boolean = false,
        public permTransfer: boolean = false,
        public permDistribute: boolean = false,
        public permCustom1: boolean = false,
        public permCustom2: boolean = false,
        public permCustom3: boolean = false,
        public permCustom4: boolean = false,
        public permCustom5: boolean = false,
        public permCustom6: boolean = false,
        public permCustom7: boolean = false,
        public permCustom8: boolean = false,
        public permCustom9: boolean = false,
        public permCustom10: boolean = false,
        public permCustom11: boolean = false,
        public permCustom12: boolean = false,
        public _links: Links = undefined
    ) { }
}
