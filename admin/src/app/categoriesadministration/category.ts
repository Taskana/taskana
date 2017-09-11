export class Category {
    constructor(
        public id: string,
        public name: string,
        public owner: string,
        public description: string,
        public priority: number,
        public serviceLevel: string) { }
}