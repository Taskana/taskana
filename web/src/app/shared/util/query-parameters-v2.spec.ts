import { asUrlQueryString } from './query-parameters-v2';

describe('asUrlQueryString', () => {
  it('should create a empty query', () => {
    expect(asUrlQueryString({})).toBe('');
  });

  it('should create a query string with one argument', () => {
    expect(asUrlQueryString({ foo: 'bar' })).toBe('?foo=bar');
  });

  it('should create a query string with multiple argument', () => {
    expect(asUrlQueryString({ foo1: 'bar1', foo2: 'bar2' })).toBe('?foo1=bar1&foo2=bar2');
  });

  it('should expand any array argument', () => {
    expect(asUrlQueryString({ foo: ['bar1', 'bar2'] })).toBe('?foo=bar1&foo=bar2');
  });

  it('should skip undefined values', () => {
    expect(asUrlQueryString({ foo: 'bar', foo1: undefined })).toBe('?foo=bar');
  });

  it('should skip undefined values in array', () => {
    expect(asUrlQueryString({ foo: ['bar1', undefined, 'bar2'] })).toBe('?foo=bar1&foo=bar2');
  });
});
