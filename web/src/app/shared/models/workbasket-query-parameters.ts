import { WorkbasketType } from './workbasket-type';
import { WorkbasketPermission } from './workbasket-permission';

export interface WorkbasketQueryFilterParameter {
  name?: string[];
  'name-like'?: string[];
  key?: string[];
  'key-like'?: string[];
  owner?: string[];
  'owner-like'?: string[];
  'description-like'?: string[];
  domain?: string[];
  type?: WorkbasketType[];
  'required-permission'?: WorkbasketPermission[];
}
