import { Page } from './page';

export class TaskHistoryEventData {
  taskHistoryId = 0;
  parentBusinessProcessId = '';
  businessProcessId = '';
  created = '';
  userId = '';
  eventType = '';
  workbasketKey = '';
  porType = '';
  porValue = '';
  domain = '';
  taskId = '';
  porCompany = '';
  porSystem = '';
  porInstance = '';
  taskClassificationKey = '';
  taskClassificationCategory = '';
  attachmentClassificationKey = '';
  custom1 = '';
  custom2 = '';
  custom3 = '';
  custom4 = '';
  comment = '';
  oldValue = '';
  newValue = '';
  oldData = '';
  newData = '';
  page = new Page();

  public constructor(init?: Partial<TaskHistoryEventData>) {
    Object.assign(this, init);
  }
}
