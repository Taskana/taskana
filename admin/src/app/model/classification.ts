export class Classification {
  constructor(public classificationId: string,
              public key: string,
              public parentId: string,
              public category: string,
              public domain: string,
              public isValidInDomain: boolean,
              public created: string,
              public modifies: string,
              public name: string,
              public description: string,
              public priority: number,
              public serviceLevel: string,
              public applicationEntryPoint: string,
              public custom1: string,
              public custom2: string,
              public custom3: string,
              public custom4: string,
              public custom5: string,
              public custom6: string,
              public custom7: string,
              public custom8: string) {
  }
}
