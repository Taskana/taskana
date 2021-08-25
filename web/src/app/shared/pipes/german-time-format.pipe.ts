import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'germanTimeFormat'
})
export class GermanTimeFormatPipe implements PipeTransform {
  transform(value: string): string {
    const dateStr = Date.parse(value);
    if (isNaN(dateStr)) return value;
    return Intl.DateTimeFormat('de', this.options).format(dateStr);
  }

  options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  } as const;
}
