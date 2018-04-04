export class Classification {
  constructor(public id: string,
    public key: string,
    public category: string,
    public type: string,
    public domain: string,
    public name: string,
    public parentId: string,
    public priority: number,
    public serviceLevel: string) {
  }
}
