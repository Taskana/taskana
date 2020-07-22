import { Links } from './links';
import { ICONTYPES } from './icon-types';

export interface Workbasket {
  workbasketId?: string;
  key?: string;
  name?: string;
  domain?: string;
  type?: ICONTYPES;
  description?: string;
  owner?: string;
  custom1?: string;
  custom2?: string;
  custom3?: string;
  custom4?: string;
  orgLevel1?: string;
  orgLevel2?: string;
  orgLevel3?: string;
  orgLevel4?: string;
  markedForDeletion?: boolean;
  created?: string;
  modified?: string;
  _links?: Links;
}

export const customFieldCount: number = 4;
