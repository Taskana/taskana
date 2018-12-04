import { Links } from './links';
import { TaskHistoryEventData } from './task-history-event';

export class TaskHistoryEventResourceData {
    public _embedded: { 'taskHistoryEventResourceList': Array<TaskHistoryEventData> }
    public _links: Links = undefined
}
