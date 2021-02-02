import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'orderBy' })
export class OrderBy implements PipeTransform {
  transform(records: Object[], sortKeys?: string[]): any {
    return records.sort((a, b) => {
      for (let i = 0; i < sortKeys.length; i++) {
        let sortKey = sortKeys[i];
        let direction = 1;
        if (sortKey.charAt(0) === '-') {
          direction = -1;
          sortKey = sortKey.substr(1);
        }
        const objectA = a[sortKey].toLowerCase();
        const objectB = b[sortKey].toLowerCase();
        if (objectA < objectB) {
          return -1 * direction;
        }
        if (objectA > objectB) {
          return direction;
        }
      }
      return 0;
    });
  }
}
