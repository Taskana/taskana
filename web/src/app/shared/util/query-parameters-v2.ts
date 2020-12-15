export function asUrlQueryString(params: Object): string {
  let query = '';

  for (const [key, value] of Object.entries(params)) {
    if (value) {
      let values: any[] = value instanceof Array ? value : [value];
      values.filter((v) => v !== undefined).forEach((v) => (query += (query ? '&' : '?') + `${key}=${v}`));
    }
  }
  return query;
}
