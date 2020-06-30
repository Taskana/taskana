import { Links } from './links';
import { ICONTYPES } from './icon-types';

export class Workbasket {
  constructor(
    public workbasketId?: string,
    public created?: string,
    public key?: string,
    public domain?: string,
    public type: ICONTYPES = ICONTYPES.PERSONAL,
    public modified?: string,
    public name?: string,
    public description?: string,
    public owner?: string,
    public custom1?: string,
    public custom2?: string,
    public custom3?: string,
    public custom4?: string,
    public orgLevel1?: string,
    public orgLevel2?: string,
    public orgLevel3?: string,
    public orgLevel4?: string,
    public _links: Links = {}
  ) {
  }
}

export const customFieldCount: number = 4;
