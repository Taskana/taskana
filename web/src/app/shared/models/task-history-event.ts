export interface TaskHistoryEventData {
  taskHistoryId: string;
  parentBusinessProcessId: string;
  businessProcessId: string;
  created: string;
  userId: string;
  eventType: string;
  workbasketKey: string;
  porType: string;
  porValue: string;
  domain: string;
  taskId: string;
  porCompany: string;
  porSystem: string;
  porInstance: string;
  taskClassificationKey: string;
  taskClassificationCategory: string;
  attachmentClassificationKey: string;
  custom1: string;
  custom2: string;
  custom3: string;
  custom4: string;
  comment: string;
  oldValue: string;
  newValue: string;
  oldData: string;
  newData: string;
}
