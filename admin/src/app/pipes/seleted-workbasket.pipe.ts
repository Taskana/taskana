import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'selectWorkbaskets' })
export class SelectWorkBasketPipe implements PipeTransform {
    transform(originArray: any, arg0: any, arg1: any): Object[] {
        let returnArray = [];
        if (!originArray) {
            return returnArray;
        }
        for (let index = originArray.length - 1; index >= 0; index--) {
            if ((arg1 && !arg0.some(elementToRemove => { return originArray[index].workbasketId === elementToRemove.workbasketId})) ||
                    !arg1 && arg0.some(elementToRemove => { return originArray[index].workbasketId === elementToRemove.workbasketId})) {
                originArray.splice(index, 1);
            }
        }
        returnArray = originArray; 
        return returnArray;
    }

    
}