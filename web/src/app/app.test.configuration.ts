
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

export const configureTests = (configure: (testBed: TestBed) => void) => {
    const testBed = getTestBed();

    if (testBed.platform == null) {
        testBed.initTestEnvironment(
            BrowserDynamicTestingModule,
            platformBrowserDynamicTesting());
    }

    configure(testBed);
    testBed.configureTestingModule({ providers: [{ provide: TaskanaEngineService, useClass: TaskanaEngineServiceMock }] });

    return testBed.compileComponents().then(() => testBed);
};
