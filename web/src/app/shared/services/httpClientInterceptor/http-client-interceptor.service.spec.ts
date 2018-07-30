import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { HttpClientInterceptor } from './http-client-interceptor.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

describe('HttpExtensionService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientModule],
			providers: [HttpClientInterceptor, ErrorModalService, RequestInProgressService]
		});
	});

	it('should be created', inject([HttpClientInterceptor], (service: HttpClientInterceptor) => {
		expect(service).toBeTruthy();
	}));
});
