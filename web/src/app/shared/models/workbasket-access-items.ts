import { Links } from './links';

export interface WorkbasketAccessItems {
  accessItemId: string;
  workbasketId: string;
  workbasketKey: string;
  accessId: string;
  accessName: string;
  permRead: boolean;
  permOpen: boolean;
  permAppend: boolean;
  permTransfer: boolean;
  permDistribute: boolean;
  permCustom1: boolean;
  permCustom2: boolean;
  permCustom3: boolean;
  permCustom4: boolean;
  permCustom5: boolean;
  permCustom6: boolean;
  permCustom7: boolean;
  permCustom8: boolean;
  permCustom9: boolean;
  permCustom10: boolean;
  permCustom11: boolean;
  permCustom12: boolean;
  _links?: Links;
}

export const customFieldCount: number = 12;
