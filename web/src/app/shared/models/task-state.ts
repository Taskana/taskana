export enum TaskState {
  READY = 'READY',
  CLAIMED = 'CLAIMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  TERMINATED = 'TERMINATED'
}

export const ALL_STATES: Map<TaskState, string> = new Map([
  [undefined, 'All'],
  [TaskState.READY, 'Ready'],
  [TaskState.CLAIMED, 'Claimed'],
  [TaskState.COMPLETED, 'Completed'],
  [TaskState.CANCELLED, 'Cancelled'],
  [TaskState.TERMINATED, 'Terminated']
]);
