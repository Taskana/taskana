export interface TaskSummary {
  taskId?: string;
  externalId?: string;
  created?: string;
  claimed?: string;
  completed?: string;
  modified?: string;
  planned?: string;
  due?: string;
  name?: string;
  creator?: string;
  note?: string;
  description?: string;
  priority?: number;
  state?: string;
  classificationSummary?: Object /* @TODO: Update to something real */;
  workbasketSummary?: Object /* @TODO: Update to something real */;
  businessProcessId?: string;
  parentBusinessProcessId?: string;
  owner?: string;
  primaryObjRef?: Object /* @TODO: Update to something real */;
  secondaryObjectReferences?: Object[] /* @TODO: Update to something real */;
  custom1?: string;
  custom2?: string;
  custom3?: string;
  custom4?: string;
  custom5?: string;
  custom6?: string;
  custom7?: string;
  custom8?: string;
  custom9?: string;
  custom10?: string;
  custom11?: string;
  custom12?: string;
  custom13?: string;
  custom14?: string;
  custom15?: string;
  custom16?: string;
  attachmentSummaries?: any[];
  read?: boolean;
  transferred?: boolean;
  count?: number;
  // [key: string]: any;
}

export interface Task extends TaskSummary {
  customAttributes?: any[];
  callbackinfo?: any[];
  attachments?: any[];
}
