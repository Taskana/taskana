// tslint:disable:max-line-length
/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
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

import { HttpClientInterceptor } from 'app/services/httpClientInterceptor/http-client-interceptor.service';
import { PermissionService } from 'app/services/permission/permission.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { DomainService } from 'app/services/domain/domain.service';
import { StartupService } from 'app/services/startup-service/startup.service';
import { AlertService } from 'app/services/alert/alert.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { TreeService } from 'app/services/tree/tree.service';


/**
 * Components
 */
import { AppComponent } from './app.component';
import { NavBarComponent } from 'app/components/nav-bar/nav-bar.component';


/**
 * Guards
 */
import { DomainGuard } from './guards/domain-guard';
import { APP_BASE_HREF } from '@angular/common';


const MODULES = [
  BrowserModule,
  FormsModule,
  TabsModule.forRoot(),
  AppRoutingModule,
  AlertModule.forRoot(),
  AngularSvgIconModule,
  HttpClientModule,
  BrowserAnimationsModule,
  ReactiveFormsModule,
  TreeModule,
  SharedModule
];

const DECLARATIONS = [
  AppComponent,
  NavBarComponent
];

export function startupServiceFactory(startupService: StartupService): Function {
  return () => startupService.load();
}

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    { provide: APP_BASE_HREF, useValue: '/' },
    DomainService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpClientInterceptor,
      multi: true
    },
    ErrorModalService,
    RequestInProgressService,
    OrientationService,
    SelectedRouteService,
    DomainGuard,
    StartupService,
    {
      provide: APP_INITIALIZER,
      useFactory: startupServiceFactory,
      deps: [StartupService],
      multi: true
    },
    AlertService,
    PermissionService,
    MasterAndDetailService,
    TreeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

// tslint:enable:max-line-length
