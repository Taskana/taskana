import { Links } from './links';
import { WorkbasketAccessItems } from './workbasket-access-items';

export interface WorkbasketAccessItemsRepresentation {
  accessItems: WorkbasketAccessItems[];
  _links?: Links;
}
