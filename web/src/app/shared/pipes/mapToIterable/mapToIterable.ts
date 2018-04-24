import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'mapToIterable'
})
export class MapToIterable implements PipeTransform {
  transform(dict: Object) {
    const result = [];
    for (const key in dict) {
      if (dict.hasOwnProperty(key)) {
        result.push({key: key, val: dict[key]});
      }
    }
    return result;
  }
}
