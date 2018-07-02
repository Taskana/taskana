import { TaskanaQueryParameters } from './query-parameters';
import { Direction } from 'app/models/sorting';

describe('TaskanaQueryParameters', () => {

    it('should create a empty query', () => {
        expect(TaskanaQueryParameters.getQueryParameters()).toBe('');
    });

    it('should create a query separated with &', () => {
        expect(TaskanaQueryParameters.getQueryParameters(TaskanaQueryParameters.KEY,
            Direction.ASC).split('&').length).toBe(2);
    });

    it('should remove last & from query', () => {
        expect(TaskanaQueryParameters.getQueryParameters(TaskanaQueryParameters.KEY,
            Direction.ASC).endsWith('?')).toBeFalsy();
    });
});
