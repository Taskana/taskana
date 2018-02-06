import {WorkbasketSummary} from './WorkbasketSummary';
export class Workbasket {
    constructor(
        public id: string,
        public created: string,
        public key: string,
        public domain: string,
        public type: string,
        public modified: string,
        public name: string,
        public description: string,        
        public owner: string,
        public custom1: string,
        public custom2: string,
        public custom3: string,
        public custom4: string,
        public orgLevel1: string,
        public orgLevel2: string,
        public orgLevel3: string,
        public orgLevel4: string, 
        public workbasketSummary: WorkbasketSummary){}
}