import { QueryPagingParameter } from './query-paging-parameter';

export class WorkbasketQueryPagingParameter implements QueryPagingParameter {
  public 'page-size' = 40;

  constructor(public page: number) {}
}
