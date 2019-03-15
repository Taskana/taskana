import { DatePipe } from '@angular/common';

export class TaskanaDate {
  public static dateFormat = 'yyyy-MM-ddTHH:mm:ss.sss';
  public static getDate(): string {
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);

    return datePipe.transform(Date.now(), this.dateFormat) + 'Z';
  }

  public static convertSimpleDate(date: Date): string {
    const dateFormat = 'yyyy-MM-dd';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);
    return datePipe.transform(date, dateFormat);
  }

  public static getDateToDisplay(date: string): string {
    return this.applyTimeZone(date);
  }

  private static applyTimeZone(date: string): string | null {
    const dateFormat = 'yyyy-MM-dd HH:mm:ss';
    const dateLocale = 'en-US';
    const datePipe = new DatePipe(dateLocale);

    return datePipe.transform(date, dateFormat, Intl.DateTimeFormat().resolvedOptions().timeZone);
  }
}
