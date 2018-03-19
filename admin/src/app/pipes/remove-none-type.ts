import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'removeEmptyType' })
export class RemoveNoneTypePipe implements PipeTransform {
    transform(value: any): Object[] {
        const returnArray = [];
        value.forEach((entry) => {
            if (entry.key !== '') {
                returnArray.push({
                    key: entry.key,
                    value: entry.value
                });
            }
        });
        return returnArray;
    }
}
