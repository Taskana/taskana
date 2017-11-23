export class Task {

    id: string;
    created: any;
    claimed: any;
    completed: any;
    modified: any;
    planned: any;
    due: any;
    name: string;
    description: string;
    priority: number;
    state: string;
    type: string;
    workbasket: string;
    owner: string;

  static create(data) {
    return new Task(data);
  }

  constructor(data) {
    this.id = data.id;
    this.created = data.created;
    this.claimed = data.claimed;
    this.completed = data.completed;
    this.modified = data.modified;
    this.planned = data.planned;
    this.due = data.due;
    this.name = data.name;
    this.description = data.description;
    this.priority = data.priority;
    this.state = data.state;
    this.type = data.type;
    this.workbasket = data.workbasket;
    this.owner = data.owner;
  }
}
