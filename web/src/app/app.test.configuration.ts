import { getTestBed, TestBed } from '@angular/core/testing';

import { BrowserDynamicTestingModule, platformBrowserDynamicTesting } from '@angular/platform-browser-dynamic/testing';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { KadaiEngineServiceMock } from './shared/services/kadai-engine/kadai-engine.mock.service';
import { KadaiEngineService } from './shared/services/kadai-engine/kadai-engine.service';
import { DomainService } from './shared/services/domain/domain.service';
import { DomainServiceMock } from './shared/services/domain/domain.service.mock';
import { RequestInProgressService } from './shared/services/request-in-progress/request-in-progress.service';
import { OrientationService } from './shared/services/orientation/orientation.service';
import { SelectedRouteService } from './shared/services/selected-route/selected-route';
import { FormsValidatorService } from './shared/services/forms-validator/forms-validator.service';
import { SharedModule } from './shared/shared.module';
import { NotificationService } from './shared/services/notifications/notification.service';

export const configureTests = (configure: (testBed: TestBed) => void) => {
  const testBed = getTestBed();

  if (testBed.platform == null) {
    testBed.initTestEnvironment(BrowserDynamicTestingModule, platformBrowserDynamicTesting());
  }

  configure(testBed);
  testBed.configureTestingModule({
    imports: [
      BrowserAnimationsModule,
      SharedModule,
      FormsModule,
      ReactiveFormsModule,
      HttpClientModule,
      AngularSvgIconModule
    ],
    providers: [
      { provide: KadaiEngineService, useClass: KadaiEngineServiceMock },
      { provide: DomainService, useClass: DomainServiceMock },
      NotificationService,
      RequestInProgressService,
      OrientationService,
      SelectedRouteService,
      FormsValidatorService
    ]
  });

  return testBed.compileComponents().then(() => testBed);
};
