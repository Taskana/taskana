import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'mapValues' })
export class MapValuesPipe implements PipeTransform {
  transform<T, V>(value: Map<T, V>, args?: any[]): { key: T; value: V }[] {
    const returnArray = [];

    if (!value) {
      return returnArray;
    }

    value.forEach((entryVal, entryKey) => {
      returnArray.push({
        key: entryKey,
        value: entryVal
      });
    });

    return returnArray;
  }
}
