import { Page } from 'app/shared/models/page';
import { Links } from '../../shared/models/links';
import { Task } from './task';

export class TaskResource {
  constructor(public tasks: Array<Task>, public _links?: Links, public page: Page = new Page()) {}
}
