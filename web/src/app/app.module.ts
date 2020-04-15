/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgxsModule } from '@ngxs/store';
import { NgxsReduxDevtoolsPluginModule } from '@ngxs/devtools-plugin';
import { AlertModule } from 'ngx-bootstrap';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TreeModule } from 'angular-tree-component';
import { SharedModule } from 'app/shared/shared.module';

/**
 * Services
 */
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { SelectedRouteService } from 'app/services/selected-route/selected-route';
import { DomainService } from 'app/services/domain/domain.service';
import { StartupService } from 'app/services/startup-service/startup.service';
import { AlertService } from 'app/services/alert/alert.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { TitlesService } from 'app/services/titles/titles.service';
import { WindowRefService } from 'app/services/window/window.service';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { NavBarComponent } from 'app/components/nav-bar/nav-bar.component';
import { UserInformationComponent } from 'app/components/user-information/user-information.component';
import { NoAccessComponent } from 'app/components/no-access/no-access.component';
import { RemoveConfirmationService } from './services/remove-confirmation/remove-confirmation.service';
import { FormsValidatorService } from './shared/services/forms/forms-validator.service';
import { UploadService } from './shared/services/upload/upload.service';
import { ErrorsService } from './services/errors/errors.service';
/**
 * Components
 */
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
/**
 * Guards
 */
import { DomainGuard } from './guards/domain.guard';
import { BusinessAdminGuard } from './guards/business-admin.guard';
import { MonitorGuard } from './guards/monitor.guard';
import { UserGuard } from './guards/user.guard';
/**
 * Store
 */
import { ClassificationCategoriesService } from './shared/services/classifications/classification-categories.service';
import { environment } from '../environments/environment';
import { STATES } from './store';

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
  SharedModule,
  NgxsModule.forRoot(STATES, { developmentMode: !environment.production }),
  NgxsReduxDevtoolsPluginModule.forRoot({ disabled: environment.production, maxAge: 25 })
];

const DECLARATIONS = [
  AppComponent,
  NavBarComponent,
  UserInformationComponent,
  NoAccessComponent,
];

export function startupServiceFactory(startupService: StartupService): () => Promise<any> {
  return (): Promise<any> => startupService.load();
}


@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WindowRefService,
    DomainService,
    GeneralModalService,
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
    TitlesService,
    TaskanaEngineService,
    RemoveConfirmationService,
    FormsValidatorService,
    UploadService,
    ErrorsService,
    ClassificationCategoriesService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
