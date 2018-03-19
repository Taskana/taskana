import { TestBed, inject, async } from '@angular/core/testing';
import { WorkbasketService } from './workbasket.service';
import { Direction } from '../shared/sort/sort.component';
import { HttpModule, Http } from '@angular/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('WorkbasketService ', () => {

	let workbasketService, httpClient;
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientModule, HttpClientTestingModule],
			providers: [WorkbasketService, HttpClient, HttpTestingController]
		});

		httpClient = TestBed.get(HttpClient);
		workbasketService = TestBed.get(WorkbasketService);

	});

	describe(' WorkbasketSummary GET method ', () => {

		beforeEach(() => {
			spyOn(httpClient, 'get').and.returnValue('');
		});

		it('should have a valid query parameter expression sortBy=key, order=asc as default', () => {
			workbasketService.getWorkBasketsSummary();
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/?sortBy=key&order=asc',
				jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=name and order=desc', () => {
			workbasketService.getWorkBasketsSummary(undefined, 'name', Direction.DESC);
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/?sortBy=name&order=desc',
				jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=name  and order=desc and descLike=some description ', () => {
			workbasketService.getWorkBasketsSummary(undefined, 'name', Direction.DESC, undefined, undefined, 'some description');
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/' +
				'?sortBy=name&order=desc&descLike=some description', jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=key, order=asc, descLike=some description and type=group ', () => {
			workbasketService.getWorkBasketsSummary(undefined, 'name', Direction.DESC,
				undefined, undefined, 'some description', undefined, undefined, 'group');
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/' +
				'?sortBy=name&order=desc&descLike=some description&type=group', jasmine.any(Object));
		});
	});
});
