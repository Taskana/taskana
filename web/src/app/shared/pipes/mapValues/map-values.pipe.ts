import {Pipe, PipeTransform} from '@angular/core';

@Pipe({ name: 'mapValues' })
export class MapValuesPipe implements PipeTransform {
    transform(value: any, args?: any[]): Object[] {
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
