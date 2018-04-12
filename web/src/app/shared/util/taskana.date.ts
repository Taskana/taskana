import {DatePipe} from '@angular/common';

export class TaskanaDate {
  public static getDate(): string {
    const dateFormat = 'yyyy-MM-ddTHH:mm:ss.sss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);
    return datePipe.transform(Date.now(), dateFormat) + 'Z';
  }
}
