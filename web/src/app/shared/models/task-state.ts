export enum TaskState {
  ALL = 'ALL',
  READY = 'READY',
  CLAIMED = 'CLAIMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  TERMINATED = 'TERMINATED'
}

export const ALL_STATES: Map<TaskState, string> = new Map([
  [TaskState.ALL, 'All'],
  [TaskState.READY, 'Ready'],
  [TaskState.CLAIMED, 'Claimed'],
  [TaskState.COMPLETED, 'Completed'],
  [TaskState.CANCELLED, 'Cancelled'],
  [TaskState.TERMINATED, 'Terminated']
]);
