import { Links } from './links';
import { TaskHistoryEventData } from './task-history-event';
import { Page } from './page';

export class TaskHistoryEventResourceData {
  public taskHistoryEvents: Array<TaskHistoryEventData>;
  public page?: Page;
  public _links: Links = {};
}
