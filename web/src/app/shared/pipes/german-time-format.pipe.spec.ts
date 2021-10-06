import { GermanTimeFormatPipe } from './german-time-format.pipe';

describe('GermanTimeFormatPipe', () => {
  it('create an instance', () => {
    const pipe = new GermanTimeFormatPipe();
    expect(pipe).toBeTruthy();
  });

  // This test currently doesn't work in GitHub CI, but runs on local machine
  // Re-enable test when developing this pipe
  it.skip('should convert ISO time to german time', () => {
    const pipe = new GermanTimeFormatPipe();
    expect(pipe.transform('2021-08-20T09:31:41Z')).toMatch('20.08.2021, 11:31:41');
  });

  it('should return input value when input is string but not a date', () => {
    const pipe = new GermanTimeFormatPipe();
    expect(pipe.transform('totally not a date')).toMatch('totally not a date');
  });
});
