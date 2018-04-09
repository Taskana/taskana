import { Links } from 'app/models/links';

export class Classification {
  constructor(public classificationId: string,
    public key: string,
    public category: string,
    public type: string,
    public domain: string,
    public name: string,
    public parentId: string,
    public priority: number,
    public serviceLevel: string,
    public _links: Links = new Links()) {
  }
}
