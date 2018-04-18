import { TestBed, inject, async, tick, fakeAsync } from '@angular/core/testing';
import { HttpModule, Http } from '@angular/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { WorkbasketService } from './workbasket.service';
import { Direction } from 'app/models/sorting';
import { DomainService } from '../domain/domain.service';
import { DomainServiceMock } from 'app/services/domain/domain.service.mock';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';


@Component({
	selector: 'taskana-dummy-detail',
	template: 'dummydetail'
})
class DummyDetailComponent {
}
const routes: Routes = [
	{ path: '', component: DummyDetailComponent }
];

xdescribe('WorkbasketService ', () => {

	let workbasketService, httpClient, domainService;
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientModule, HttpClientTestingModule, RouterTestingModule.withRoutes(routes)],
			providers: [
				WorkbasketService,
				HttpClient,
				HttpTestingController,
				DomainService,
				RequestInProgressService],
			declarations: [DummyDetailComponent]
		});

		httpClient = TestBed.get(HttpClient);
		workbasketService = TestBed.get(WorkbasketService);
		domainService = TestBed.get(DomainService);

	});

	describe(' WorkbasketSummary GET method ', () => {

		beforeEach(() => {
			spyOn(httpClient, 'get').and.returnValue('');
		});

		it('should have a valid query parameter expression sortBy=key, order=asc as default', () => {
			workbasketService.getWorkBasketsSummary(true);
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/?sortBy=key&order=asc&page=1&pagesize=9',
				jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=name and order=desc', () => {
			workbasketService.getWorkBasketsSummary(true, 'name', Direction.DESC);
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/?sortBy=name&order=desc&page=1&pagesize=9',
				jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=name  and order=desc and descLike=some description ', () => {
			workbasketService.getWorkBasketsSummary(true, 'name', Direction.DESC, undefined, undefined, 'some description');
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/?sortBy=name&order=desc' +
				'&descLike=some description&page=1&pagesize=9', jasmine.any(Object));
		});

		it('should have a valid query parameter expression with sortBy=key, order=asc, descLike=some description and type=group ', () => {
			workbasketService.getWorkBasketsSummary(true, 'name', Direction.DESC,
				undefined, undefined, 'some description', undefined, undefined, 'group');
			expect(httpClient.get).toHaveBeenCalledWith('http://localhost:8080/v1/workbaskets/' +
				'?sortBy=name&order=desc&descLike=some description&type=group&page=1&pagesize=9', jasmine.any(Object));
		});
	});
});
