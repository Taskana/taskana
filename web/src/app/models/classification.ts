import { Links } from 'app/models/links';

export class Classification {
  constructor(public classificationId?: string, // newly created classifications don't have an id yet.
    public key?: string,
    public category?: string,
    public type?: string,
    public domain?: string,
    public name?: string,
    public parentId?: string,
    public priority?: number,
    public serviceLevel?: string,
    public _links?: Links) {
  }
}
