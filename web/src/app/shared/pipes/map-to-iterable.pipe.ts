import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'mapToIterable'
})
export class MapToIterable implements PipeTransform {
  transform(dict: Object) {
    const result = [];
    Object.keys(dict).forEach((key) => {
      result.push({ key, val: dict[key] });
    });
    return result;
  }
}
