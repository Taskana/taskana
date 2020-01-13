import { Links } from './links';
import { TaskHistoryEventData } from './task-history-event';

export class TaskHistoryEventResourceData {
  public taskHistoryEvents: Array<TaskHistoryEventData>;
  public _links: Links = new Links();
}
