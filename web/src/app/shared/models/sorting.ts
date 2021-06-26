export enum Direction {
  ASC = 'ASCENDING',
  DESC = 'DESCENDING'
}

export interface Sorting<T> {
  'sort-by': T;
  order: Direction;
}

export enum TaskQuerySortParameter {
  CLASSIFICATION_KEY = 'CLASSIFICATION_KEY',
  POR_TYPE = 'POR_TYPE',
  POR_VALUE = 'POR_VALUE',
  STATE = 'STATE',
  NAME = 'NAME',
  DUE = 'DUE',
  PLANNED = 'PLANNED',
  PRIORITY = 'PRIORITY'
}

export const TASK_SORT_PARAMETER_NAMING: Map<TaskQuerySortParameter, string> = new Map([
  [TaskQuerySortParameter.NAME, 'Name'],
  [TaskQuerySortParameter.PRIORITY, 'Priority'],
  [TaskQuerySortParameter.DUE, 'Due'],
  [TaskQuerySortParameter.PLANNED, 'Planned']
]);

export enum WorkbasketQuerySortParameter {
  NAME = 'NAME',
  KEY = 'KEY',
  OWNER = 'OWNER',
  TYPE = 'TYPE',
  DESCRIPTION = 'DESCRIPTION'
}

export const WORKBASKET_SORT_PARAMETER_NAMING: Map<WorkbasketQuerySortParameter, string> = new Map([
  [WorkbasketQuerySortParameter.NAME, 'Name'],
  [WorkbasketQuerySortParameter.KEY, 'Key'],
  [WorkbasketQuerySortParameter.DESCRIPTION, 'Description'],
  [WorkbasketQuerySortParameter.OWNER, 'Owner'],
  [WorkbasketQuerySortParameter.TYPE, 'Type']
]);

export enum WorkbasketAccessItemQuerySortParameter {
  WORKBASKET_KEY = 'WORKBASKET_KEY',
  ACCESS_ID = 'ACCESS_ID'
}

export const WORKBASKET_ACCESS_ITEM_SORT_PARAMETER_NAMING: Map<WorkbasketAccessItemQuerySortParameter, string> =
  new Map([
    [WorkbasketAccessItemQuerySortParameter.ACCESS_ID, 'Access id'],
    [WorkbasketAccessItemQuerySortParameter.WORKBASKET_KEY, 'Workbasket Key']
  ]);

export enum ClassificationQuerySortParameter {
  DOMAIN = 'DOMAIN',
  KEY = 'KEY',
  CATEGORY = 'CATEGORY',
  NAME = 'NAME'
}

export enum TaskHistoryQuerySortParameter {
  TASK_HISTORY_EVENT_ID = 'TASK_HISTORY_EVENT_ID',
  BUSINESS_PROCESS_ID = 'BUSINESS_PROCESS_ID',
  PARENT_BUSINESS_PROCESS_ID = 'PARENT_BUSINESS_PROCESS_ID',
  TASK_ID = 'TASK_ID',
  EVENT_TYPE = 'EVENT_TYPE',
  CREATED = 'CREATED',
  USER_ID = 'USER_ID',
  DOMAIN = 'DOMAIN',
  WORKBASKET_KEY = 'WORKBASKET_KEY',
  POR_COMPANY = 'POR_COMPANY',
  POR_SYSTEM = 'POR_SYSTEM',
  POR_INSTANCE = 'POR_INSTANCE',
  POR_TYPE = 'POR_TYPE',
  POR_VALUE = 'POR_VALUE',
  TASK_CLASSIFICATION_KEY = 'TASK_CLASSIFICATION_KEY',
  TASK_CLASSIFICATION_CATEGORY = 'TASK_CLASSIFICATION_CATEGORY',
  ATTACHMENT_CLASSIFICATION_KEY = 'ATTACHMENT_CLASSIFICATION_KEY',
  CUSTOM_1 = 'CUSTOM_1',
  CUSTOM_2 = 'CUSTOM_2',
  CUSTOM_3 = 'CUSTOM_3',
  CUSTOM_4 = 'CUSTOM_4'
}
