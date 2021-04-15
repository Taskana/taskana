export class SetFilterExpansion {
  static readonly type = '[Task list toolbar] Expand or collapse the Task filter';
  constructor(public isExpanded?: boolean) {}
}

export class CalculateNumberOfCards {
  static readonly type = '[Task master] Calculate number of cards for task list';
}
