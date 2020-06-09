import { Links } from './links';
import { ICONTYPES } from './icon-types';

export class Workbasket {
  workbasketId?: string;
  created?: string;
  key?: string;
  domain?: string;
  type: ICONTYPES;
  modified?: string;
  name?: string;
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
  _links: Links;
  markedForDeletion?: boolean;
}

export const customFieldCount: number = 4;
