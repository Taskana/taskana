import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'numberToArray' })
export class NumberToArray implements PipeTransform {
  transform(index: number): Array<number> {
    return Array.from(Array(index), (x, i) => i);
  }
}
