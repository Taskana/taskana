import { Classification } from 'app/models/classification';

export class TreeNode extends Classification {
  constructor(public id: string = '',
    public key: string = '',
    public category: string = '',
    public type: string = '',
    public domain: string = '',
    public name: string = '',
    public parentId: string = '',
    public priority: number = 0,
    public serviceLevel: string = '',
    public children: Array<TreeNode> = undefined) {
    super(id, key, category, type, domain, name, parentId, priority, serviceLevel);
  }
}
