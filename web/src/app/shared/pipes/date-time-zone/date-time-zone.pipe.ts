import {Pipe, PipeTransform} from '@angular/core';
import {TaskanaDate} from '../../util/taskana.date';

@Pipe({
  name: 'dateTimeZone'
})
export class DateTimeZonePipe implements PipeTransform {

  private datesMap = new Map<string, string>();

  transform(value: any, args?: any): any {

    let date = this.datesMap.get(value);
    if (!date) {
      date = TaskanaDate.getDateToDisplay(value);
      this.datesMap.set(value, date);
    }
    return date;
  }
}
