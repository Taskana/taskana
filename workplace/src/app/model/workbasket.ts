import {Links} from './links';

export class Workbasket {

  constructor(public workbasketId: string,
              public created: string = undefined,
              public key: string = undefined,
              public domain: string = undefined,
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
