import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'removeNoneType'})
export class RemoveNoneTypePipe implements PipeTransform {
    transform(value: any): Object[] {
        let returnArray = [];
        value.forEach((entry) => {
            if(entry.key !== 'NONE')
            returnArray.push({
                key: entry.key,
                value: entry.value
            });
        });
        return returnArray;
    }
}