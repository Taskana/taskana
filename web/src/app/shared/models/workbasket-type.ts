export enum WorkbasketType {
  PERSONAL = 'PERSONAL',
  GROUP = 'GROUP',
  CLEARANCE = 'CLEARANCE',
  TOPIC = 'TOPIC'
}

export const ALL_TYPES: Map<WorkbasketType, string> = new Map([
  [undefined, 'All'],
  [WorkbasketType.PERSONAL, 'Personal'],
  [WorkbasketType.GROUP, 'Group'],
  [WorkbasketType.CLEARANCE, 'Clearance'],
  [WorkbasketType.TOPIC, 'Topic']
]);
