import {Links} from '../../models/links';
import {Task} from './task';

export class TaskResource {
  constructor(public _embedded: { 'tasks': Array<Task> } = { 'tasks': [] },
              public _links: Links = undefined) {}
}
