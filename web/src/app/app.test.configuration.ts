
import {
    getTestBed,
    TestBed,
} from '@angular/core/testing';

import {
    BrowserDynamicTestingModule,
    platformBrowserDynamicTesting,
} from '@angular/platform-browser-dynamic/testing';

import { TaskanaEngineServiceMock } from './services/taskana-engine/taskana-engine.mock.service';
import { TaskanaEngineService } from './services/taskana-engine/taskana-engine.service';
import { DomainService } from './services/domain/domain.service';
import { DomainServiceMock } from './services/domain/domain.service.mock';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';
import { AlertService } from './services/alert/alert.service';
import { ErrorModalService } from './services/errorModal/error-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { OrientationService } from './services/orientation/orientation.service';
import { SelectedRouteService } from './services/selected-route/selected-route';
import { FormsValidatorService } from './shared/services/forms/forms-validator.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from './shared/shared.module';


export const configureTests = (configure: (testBed: TestBed) => void) => {
    const testBed = getTestBed();

    if (testBed.platform == null) {
        testBed.initTestEnvironment(
            BrowserDynamicTestingModule,
            platformBrowserDynamicTesting());
    }

    configure(testBed);
    testBed.configureTestingModule({
        imports: [BrowserAnimationsModule, SharedModule],
        providers: [{ provide: TaskanaEngineService, useClass: TaskanaEngineServiceMock },
        { provide: DomainService, useClass: DomainServiceMock }, CustomFieldsService, RemoveConfirmationService,
            AlertService, ErrorModalService, RequestInProgressService, OrientationService, SelectedRouteService, FormsValidatorService]
    });

    return testBed.compileComponents().then(() => testBed);
};
