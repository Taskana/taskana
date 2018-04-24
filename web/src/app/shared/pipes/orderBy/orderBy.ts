import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'orderBy'})

export class OrderBy implements PipeTransform {
  transform(records: Array<Object>, sortKeys?: string[]): any {
    return records.sort(function (a, b) {
      for (let i = 0; i < sortKeys.length; i++) {
        let sortKey = sortKeys[i];
        let direction = 1;
        if (sortKey.charAt(0) === '-') {
          direction = -1;
          sortKey = sortKey.substr(1);
        }
        if (a[sortKey] < b[sortKey]) {
          return -1 * direction;
        } else if (a[sortKey] > b[sortKey]) {
          return direction;
        }
      }
      return 0;
    });
  };
}
