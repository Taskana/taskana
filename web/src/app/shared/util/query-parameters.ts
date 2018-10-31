export class TaskanaQueryParameters {
  // Sorting
  static SORTBY = 'sort-by';
  static ORDER = 'order';

  // Filtering
  static NAME = 'name';
  static NAMELIKE = 'name-like';
  static DESCLIKE = 'description-like';
  static OWNER = 'owner';
  static OWNERLIKE = 'owner-like';
  static TYPE = 'type';
  static KEY = 'key';
  static WORKBASKET_KEY = 'workbasket-key';
  static KEYLIKE = 'key-like';
  static PRIORITY = 'priority';
  static STATELIKE = 'state-like';
  static WORKBASKET_ID = 'workbasket-id';

  // Access
  static REQUIREDPERMISSION = 'required-permission';
  static ACCESSIDS = 'access-ids';
  static ACCESSIDLIKE = 'access-id-like';
  static WORKBASKETKEYLIKE = 'workbasket-key-like';

  // Pagination
  static PAGE = 'page';
  static PAGESIZE = 'page-size';
  static page = 1;
  static pageSize = 9;

  // Domain
  static DOMAIN = 'domain';

  public static getQueryParameters(
    sortBy: string = undefined,
    order: string = undefined,
    name: string = undefined,
    nameLike: string = undefined,
    descLike: string = undefined,
    owner: string = undefined,
    ownerLike: string = undefined,
    type: string = undefined,
    key: string = undefined,
    keyLike: string = undefined,
    requiredPermission: string = undefined,
    page: number = undefined,
    pageSize: number = undefined,
    domain: string = undefined,
    accessIds: string = undefined,
    accessIdLike: string = undefined,
    workbasketKeyLike: string = undefined,
    basketId: string = undefined,
    priority: string = undefined,
    stateLike: string = undefined,
  ): string {
    let query = '?';
    query += sortBy ? `${this.SORTBY}=${sortBy}&` : '';
    query += order ? `${this.ORDER}=${order}&` : '';
    query += name ? `${this.NAME}=${name}&` : '';
    query += nameLike ? `${this.NAMELIKE}=${nameLike}&` : '';
    query += descLike ? `${this.DESCLIKE}=${descLike}&` : '';
    query += owner ? `${this.OWNER}=${owner}&` : '';
    query += ownerLike ? `${this.OWNERLIKE}=${ownerLike}&` : '';
    query += basketId ? `${this.WORKBASKET_ID}=${basketId}&` : '';
    query += priority ? `${this.PRIORITY}=${priority}&` : '';
    query += stateLike ? `${this.STATELIKE}=${stateLike}&` : '';
    query += type ? `${this.TYPE}=${type}&` : '';
    query += key ? `${this.KEY}=${key}&` : '';
    query += keyLike ? `${this.KEYLIKE}=${keyLike}&` : '';
    query += requiredPermission
      ? `${this.REQUIREDPERMISSION}=${requiredPermission}&`
      : '';
    query += page ? `${this.PAGE}=${page}&` : '';
    query += pageSize ? `${this.PAGESIZE}=${pageSize}&` : '';
    query += domain ? `${this.DOMAIN}=${domain}&` : '';
    query += accessIds ? `${this.ACCESSIDS}=${accessIds}&` : '';
    query += accessIdLike ? `${this.ACCESSIDLIKE}=${accessIdLike}&` : '';
    query += workbasketKeyLike
      ? `${this.WORKBASKETKEYLIKE}=${workbasketKeyLike}&`
      : '';

    if (query.lastIndexOf('&') === query.length - 1) {
      query = query.slice(0, query.lastIndexOf('&'));
    }
    return query === '?' ? '' : query;
  }
}
