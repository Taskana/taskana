export enum Direction {
  ASC = 'asc',
  DESC = 'desc'
}

export class Sorting {
  sortBy: string;
  sortDirection: string;
  constructor(sortBy: string = 'key', sortDirection: Direction = Direction.ASC) {
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
  }
}
