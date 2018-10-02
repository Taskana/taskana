import {Links} from 'app/models/links';

export class Classification {
  constructor(public classificationId: string = undefined, // newly created classifications don't have an id yet.
              public key: string = undefined,
              public category: string = undefined,
              public type: string = undefined,
              public domain: string = undefined,
              public name: string = undefined,
              public parentId: string = undefined,
              public priority: number = undefined,
              public serviceLevel: string = undefined,
              public _links: Links = new Links()) {
  }
}
