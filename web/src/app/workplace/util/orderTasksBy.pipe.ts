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
              return _compareString(a[column], b[column]);
            } else {
              return _compareNumber(a[column], b[column]);
            }
        });
        return value;

        function _compareString(a: string, b: string): number {
            if (a.toLowerCase() < b.toLowerCase()) {
                return -1;
            } else if (a.toLowerCase() > b.toLowerCase()) {
                return 1;
            } else {
                return 0;
            }
        }

        function _compareNumber(a: number, b: number): number {
            return _compareString(a.toString(), b.toString());
        }
    }
}
