import { Classification } from 'app/shared/models/classification';

export interface TreeNodeModel extends Classification {
  children: TreeNodeModel[];
}
