import {Links} from '../../models/links';
import {Task} from './task';
import { Page } from 'app/models/page';

export class TaskResource {
  constructor(public tasks: Array<Task>,
              public _links: Links = undefined,
              public page: Page = new Page()) {}
}
