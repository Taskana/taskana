export enum Side {
  AVAILABLE,
  SELECTED
}

export interface AllSelected {
  value: boolean;
  side?: Side;
}
