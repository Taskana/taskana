import { Pipe, PipeTransform } from '@angular/core';
import {TaskanaDate} from '../../util/taskana.date';

@Pipe({
  name: 'datePipe'
})
export class DatePipe implements PipeTransform {
  transform(date: string, target: string) {
    if (date) {
      return target === 'ISO' ? TaskanaDate.getISODate(new Date(date)) : TaskanaDate.convertSimpleDate(new Date(date));
    }
    return '';
  }
}
