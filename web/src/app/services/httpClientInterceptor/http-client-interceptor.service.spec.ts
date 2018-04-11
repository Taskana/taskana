import { TestBed, inject } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { HttpClientInterceptor } from './http-client-interceptor.service';
import { PermissionService } from 'app/services/permission/permission.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

describe('HttpExtensionService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientModule, HttpModule],
			providers: [HttpClientInterceptor, PermissionService, ErrorModalService, RequestInProgressService]
		});
	});

	it('should be created', inject([HttpClientInterceptor], (service: HttpClientInterceptor) => {
		expect(service).toBeTruthy();
	}));
});
