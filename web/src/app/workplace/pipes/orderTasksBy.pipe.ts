import { Pipe, PipeTransform } from '@angular/core';

import { Task } from '../models/task';

@Pipe({
  name: 'orderTasksBy'
})
export class OrderTasksByPipe implements PipeTransform {
  transform(value: Task[], column: string) {
    if (value === null) { return null; }
    value.sort((a, b) => {
      if (typeof a[column] === 'string') {
        return compareString(a[column], b[column]);
      }
      return compareNumber(a[column], b[column]);
    });
    return value;

    function compareString(a: string, b: string): number {
      if (a.toLowerCase() < b.toLowerCase()) {
        return -1;
      } if (a.toLowerCase() > b.toLowerCase()) {
        return 1;
      }
      return 0;
    }

    function compareNumber(a: number, b: number): number {
      return compareString(a.toString(), b.toString());
    }
  }
}
