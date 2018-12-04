import { Page } from './page';

export class TaskHistoryEventData {
    taskHistoryId = 0;
    taskId = '';
    parentBusinessProcessId = '';
    businessProcessId = '';
    eventType = '';
    created = '';
    userId = '';
    domain = '';
    workbasketKey = '';
    porCompany = '';
    porSystem = '';
    porInstance = '';
    porType = '';
    porValue = '';
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
}
