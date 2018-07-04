import { LinksClassification } from 'app/models/links-classfication';

export class ClassificationDefinition {
  constructor(public classificationId: string = undefined,
    public key: string = undefined,
    public parentId: string = undefined,
    public parentKey: string = undefined,
    public category: string = undefined,
    public domain: string = undefined,
    public type: string = undefined,
    public isValidInDomain: boolean = undefined,
    public created: string = undefined,
    public modified: string = undefined,
    public name: string = undefined,
    public description: string = undefined,
    public priority: number = undefined,
    public serviceLevel: string = undefined,
    public applicationEntryPoint: string = undefined,
    public custom1: string = undefined,
    public custom2: string = undefined,
    public custom3: string = undefined,
    public custom4: string = undefined,
    public custom5: string = undefined,
    public custom6: string = undefined,
    public custom7: string = undefined,
    public custom8: string = undefined,
    public _links: LinksClassification = new LinksClassification()) {
  }
}
