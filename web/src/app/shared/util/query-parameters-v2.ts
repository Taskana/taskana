export function asUrlQueryString(params: Object): string {
  let query = '';

  for (const [key, value] of Object.entries(params)) {
    if (value) {
      let values: any[] = value instanceof Array ? value : [value];
      values
        .filter((v) => v !== undefined)
        .forEach((v) => (query += (query ? '&' : '?') + `${key}=${convertValue(v)}`));
    }
  }
  return query;
}

function convertValue(value: any) {
  if (value instanceof Object) {
    return encodeURIComponent(JSON.stringify(value));
  }
  return value;
}
