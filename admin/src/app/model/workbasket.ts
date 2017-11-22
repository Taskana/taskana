export class Workbasket {
    constructor(
        public id: string,
        public created: string,
        public modified: string,
        public name: string,
        public description: string,
        public owner: string,
        public distributionTargets: [string]) { }
}