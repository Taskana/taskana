import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'spreadNumber' })
export class SpreadNumberPipe implements PipeTransform {
  transform(maxPageNumber: number, currentIndex: number, maxArrayElements: number): number[] {
    if (maxPageNumber <= maxArrayElements) {
      return [...Array(maxPageNumber).keys()];
    }

    let firstIndex = currentIndex - Math.floor(maxArrayElements / 2);
    firstIndex = Math.max(firstIndex, 0);
    const lastIndex = firstIndex + maxArrayElements;
    firstIndex = lastIndex > maxPageNumber ? maxPageNumber - maxArrayElements : firstIndex;

    return [...Array(maxArrayElements).keys()].map(x => x + firstIndex);
  }
}
