import {Links} from '../../models/links';
import {Task} from './task';
import { Page } from 'app/models/page';

export class TaskResource {
  constructor(public _embedded: { 'tasks': Array<Task> } = { 'tasks': [] },
              public _links: Links = undefined,
              public page: Page = new Page()) {}
}
