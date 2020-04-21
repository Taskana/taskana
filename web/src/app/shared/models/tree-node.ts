import { Classification } from 'app/shared/models/classification';

export class TreeNodeModel extends Classification {
  constructor(public id?: string,
    public key?: string,
    public category?: string,
    public type?: string,
    public domain?: string,
    public name?: string,
    public parentId?: string,
    public priority?: number,
    public serviceLevel?: string,
    public children: Array<TreeNodeModel> = []) {
    super(id, key, category, type, domain, name, parentId, priority, serviceLevel);
  }
}
