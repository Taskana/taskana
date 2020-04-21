import { Direction } from 'app/shared/models/sorting';
import { QueryParameters } from 'app/shared/models/query-parameters';
import { TaskanaQueryParameters } from './query-parameters';

describe('TaskanaQueryParameters', () => {
  beforeAll(() => {
    TaskanaQueryParameters.page = 1;
    TaskanaQueryParameters.pageSize = 9;
  });

  it('should create a empty query', () => {
    delete TaskanaQueryParameters.page;
    delete TaskanaQueryParameters.pageSize;
    expect(TaskanaQueryParameters.getQueryParameters(new QueryParameters())).toBe('?');
    TaskanaQueryParameters.page = 1;
    TaskanaQueryParameters.pageSize = 9;
  });

  it('should create a query with pagin information', () => {
    expect(TaskanaQueryParameters.getQueryParameters(new QueryParameters())).toBe('?page=1&page-size=9');
  });

  it('should create a query separated with &', () => {
    const parameters = new QueryParameters();
    parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
    parameters.SORTDIRECTION = Direction.ASC;
    expect(TaskanaQueryParameters.getQueryParameters(parameters).split('&').length).toBe(4);
  });

  it('should remove last & from query', () => {
    const parameters = new QueryParameters();
    parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
    parameters.SORTDIRECTION = Direction.ASC;
    expect(TaskanaQueryParameters.getQueryParameters(parameters).endsWith('?')).toBeFalsy();
  });
});
