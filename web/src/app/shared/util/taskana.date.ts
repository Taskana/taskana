import { DatePipe } from '@angular/common';

export class TaskanaDate {
  public static getDate(): string {
    const dateFormat = 'yyyy-MM-ddTHH:mm:ss.sss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);

    return datePipe.transform(Date.now(), dateFormat) + 'Z';
  }

  public static convertSimpleDate(date: Date): string {
    const dateFormat = 'yyyy-MM-dd';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);
    return datePipe.transform(date, dateFormat);
  }

  public static getDateToDisplay(date: string): string {
    const dateFormat = 'yyyy-MM-dd HH:mm:ss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);

    return datePipe.transform(date, dateFormat);
  }

  public static convertToBrowserTimeZone(date: Date): string {
    return date.toLocaleString('en-US', { timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone })
  }
}
