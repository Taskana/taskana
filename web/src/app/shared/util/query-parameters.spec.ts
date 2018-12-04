import { TaskanaQueryParameters } from './query-parameters';
import { Direction } from 'app/models/sorting';
import { QueryParametersModel } from 'app/models/query-parameters';

describe('TaskanaQueryParameters', () => {

    it('should create a empty query', () => {
        TaskanaQueryParameters.page = undefined;
        TaskanaQueryParameters.pageSize = undefined;
        expect(TaskanaQueryParameters.getQueryParameters(new QueryParametersModel())).toBe('?');
        TaskanaQueryParameters.page = 1;
        TaskanaQueryParameters.pageSize = 9;
    });

    it('should create a query with pagin information', () => {
        expect(TaskanaQueryParameters.getQueryParameters(new QueryParametersModel())).toBe('?page=1&page-size=9');
    });

    it('should create a query separated with &', () => {

        const parameters = new QueryParametersModel();
        parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
        parameters.SORTDIRECTION = Direction.ASC;
        expect(TaskanaQueryParameters.getQueryParameters(parameters).split('&').length).toBe(4);
    });

    it('should remove last & from query', () => {
        const parameters = new QueryParametersModel();
        parameters.SORTBY = TaskanaQueryParameters.parameters.KEY;
        parameters.SORTDIRECTION = Direction.ASC;
        expect(TaskanaQueryParameters.getQueryParameters(parameters).endsWith('?')).toBeFalsy();
    });
});
