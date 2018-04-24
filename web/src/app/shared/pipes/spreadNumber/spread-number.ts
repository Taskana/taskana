import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'spreadNumber' })
export class SpreadNumberPipe implements PipeTransform {
    transform(value: number, currentIndex: number, maxArrayElements: number, maxPageNumber: number): number[] {
        const returnArray = new Array();
        if (maxPageNumber <= 5) {
            for (let i = 0; i < maxPageNumber; i++) {
                returnArray.push(i);
            }
            return returnArray;
        }

        let minArrayValue = (currentIndex - maxArrayElements / 2);
        let maxArrayValue = (currentIndex + maxArrayElements / 2);
        let leftDifference = 0;
        let rightDifference = 0;


        if (minArrayValue < 0) { leftDifference = Math.abs(minArrayValue); minArrayValue = 0; }
        if (maxArrayValue > maxPageNumber) {
            rightDifference = maxArrayValue - maxPageNumber;
            maxArrayValue = maxPageNumber;
        }
        const minIndex = (minArrayValue - rightDifference) <= 0 ? 0 : minArrayValue - rightDifference;
        const maxIndex = (maxArrayValue + leftDifference) > maxPageNumber ? maxPageNumber : maxArrayValue + leftDifference;

        for (let i = minIndex; i < maxIndex; i++) {
            returnArray.push(i);
        }
        return returnArray;
    }
}
