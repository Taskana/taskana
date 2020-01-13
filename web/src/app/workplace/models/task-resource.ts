import { Page } from 'app/models/page';
import { Links } from '../../models/links';
import { Task } from './task';

export class TaskResource {
  constructor(public tasks: Array<Task>,
    public _links?: Links,
    public page: Page = new Page()) {}
}
