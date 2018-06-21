
import {
    getTestBed,
    ComponentFixtureAutoDetect,
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

export const configureTests = (configure: (testBed: TestBed) => void) => {
    const testBed = getTestBed();

    if (testBed.platform == null) {
        testBed.initTestEnvironment(
            BrowserDynamicTestingModule,
            platformBrowserDynamicTesting());
    }

    configure(testBed);
    testBed.configureTestingModule({
        providers: [{ provide: TaskanaEngineService, useClass: TaskanaEngineServiceMock },
        { provide: DomainService, useClass: DomainServiceMock }]
    });

    return testBed.compileComponents().then(() => testBed);
};
