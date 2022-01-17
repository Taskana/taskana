export enum WorkbasketType {
  ALL = 'ALL',
  PERSONAL = 'PERSONAL',
  GROUP = 'GROUP',
  CLEARANCE = 'CLEARANCE',
  TOPIC = 'TOPIC'
}

export const ALL_TYPES: Map<WorkbasketType, string> = new Map([
  [WorkbasketType.ALL, 'All'],
  [WorkbasketType.PERSONAL, 'Personal'],
  [WorkbasketType.GROUP, 'Group'],
  [WorkbasketType.CLEARANCE, 'Clearance'],
  [WorkbasketType.TOPIC, 'Topic']
]);
