import { DateTimeZonePipe } from './date-time-zone.pipe';

describe('DateTimeZonePipe', () => {
  it('create an instance', () => {
    const pipe = new DateTimeZonePipe();
    expect(pipe).toBeTruthy();
  });
});
