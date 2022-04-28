export class GetTasks {
  static readonly type = '[TaskList] Get Tasks';
}

export class GetTask {
  static readonly type = '[TaskDetails] Get Task Details from Backend by its Id';

  constructor(public taskId: string) {}
}
