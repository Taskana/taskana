// tslint:disable:max-line-length
/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AlertModule } from 'ngx-bootstrap';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TreeModule } from 'angular-tree-component';
import { SharedModule } from 'app/shared/shared.module';


/**
 * Services
 */

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { DomainService } from 'app/services/domain/domain.service';
import { StartupService } from 'app/services/startup-service/startup.service';
import { AlertService } from 'app/services/alert/alert.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { TreeService } from 'app/services/tree/tree.service';
import { TitlesService } from 'app/services/titles/titles.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { WindowRefService } from 'app/services/window/window.service';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { RemoveConfirmationService } from './services/remove-confirmation/remove-confirmation.service';
import { FormsValidatorService } from './shared/services/forms/forms-validator.service';



/**
 * Components
 */
import { AppComponent } from './app.component';
import { NavBarComponent } from 'app/components/nav-bar/nav-bar.component';
import { UserInformationComponent } from 'app/components/user-information/user-information.component';
import { NoAccessComponent } from 'app/components/no-access/no-access.component';

/**
 * Guards
 */
import { DomainGuard } from './guards/domain-guard';
import { BusinessAdminGuard } from './guards/business-admin-guard';
import { MonitorGuard } from './guards/monitor-guard';
import { UserGuard } from './guards/user-guard';
import { APP_BASE_HREF } from '@angular/common';


const MODULES = [
  TabsModule.forRoot(),
  AlertModule.forRoot(),
  BrowserModule,
  FormsModule,
  AppRoutingModule,
  AngularSvgIconModule,
  HttpClientModule,
  BrowserAnimationsModule,
  ReactiveFormsModule,
  TreeModule,
  SharedModule
];

const DECLARATIONS = [
  AppComponent,
  NavBarComponent,
  UserInformationComponent,
  NoAccessComponent,
];

export function startupServiceFactory(startupService: StartupService): () => Promise<any> {
  return (): Promise<any> => {
    return startupService.load()
  };
}


@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WindowRefService,
    { provide: APP_BASE_HREF, useValue: '/' },
    DomainService,
    ErrorModalService,
    RequestInProgressService,
    OrientationService,
    SelectedRouteService,
    DomainGuard,
    BusinessAdminGuard,
    MonitorGuard,
    UserGuard,
    StartupService,
    {
      provide: APP_INITIALIZER,
      useFactory: startupServiceFactory,
      deps: [StartupService],
      multi: true
    },
    AlertService,
    MasterAndDetailService,
    TreeService,
    TitlesService,
    CustomFieldsService,
    TaskanaEngineService,
    RemoveConfirmationService,
    FormsValidatorService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

// tslint:enable:max-line-length
