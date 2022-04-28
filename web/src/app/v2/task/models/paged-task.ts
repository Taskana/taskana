import { TaskSummary } from './task';

export interface PagedTaskSummary {
  tasks: TaskSummary[];
  _links?: Object /* @TODO Use real object */;
  page?: Object /* @TODO Use real object */;
}
