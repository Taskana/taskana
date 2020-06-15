import { Links } from './links';
import { ICONTYPES } from './icon-types';

export class Workbasket {
  workbasketId?: string;
  key?: string;
  name?: string;
  domain?: string;
  type: ICONTYPES;
  description?: string;
  owner?: string;
  created?: string;
  modified?: string;
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
