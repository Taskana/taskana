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
import { TreeModule } from 'angular-tree-component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
/**
 * Components
 */
import { AppComponent } from './app.component';
import { WorkbasketListComponent } from './administration/workbasket/master/list/workbasket-list.component';
import { WorkbasketListToolbarComponent } from './administration/workbasket/master/list/workbasket-list-toolbar/workbasket-list-toolbar.component'
import { WorkbasketDetailsComponent } from './administration/workbasket/details/workbasket-details.component';
import { WorkbasketInformationComponent } from './administration/workbasket/details/information/workbasket-information.component';
import { DistributionTargetsComponent } from './administration/workbasket/details/distribution-targets/distribution-targets.component';
import { DualListComponent } from './administration/workbasket/details/distribution-targets/dual-list/dual-list.component';
import { AccessItemsComponent } from './administration/workbasket/details/access-items/access-items.component';
import { NoAccessComponent } from './administration/workbasket/details/noAccess/no-access.component';
import { SpinnerComponent } from './shared/spinner/spinner.component';
import { FilterComponent } from './shared/filter/filter.component';
import { IconTypeComponent } from './shared/type-icon/icon-type.component';
import { AlertComponent } from './shared/alert/alert.component';
import { SortComponent } from './shared/sort/sort.component';
import { GeneralMessageModalComponent } from './shared/general-message-modal/general-message-modal.component';
import { PaginationComponent } from './administration/workbasket/master/list/pagination/pagination.component';
import { ClassificationListComponent } from './administration/classification/master/list/classification-list.component';
import { ClassificationDetailsComponent } from './administration/classification/details/classification-details.component';
import { ImportExportComponent } from './shared/import-export/import-export.component';
import { MasterAndDetailComponent } from './shared/master-and-detail/master-and-detail.component';
import { ClassificationTypesSelectorComponent } from './shared/classification-types-selector/classification-types-selector.component';
import { TaskanaTreeComponent } from './shared/tree/tree.component';
import { NavBarComponent } from './shared/nav-bar/nav-bar.component';

/**
 * Services
 */
import { WorkbasketService } from './services/workbasket/workbasket.service';
import { MasterAndDetailService } from './services/masterAndDetail/master-and-detail.service';
import { HttpClientInterceptor } from './services/httpClientInterceptor/http-client-interceptor.service';
import { PermissionService } from './services/permission/permission.service';
import { AlertService } from './services/alert/alert.service';
import { ErrorModalService } from './services/errorModal/error-modal.service';
import { RequestInProgressService } from './services/requestInProgress/request-in-progress.service';
import { SavingWorkbasketService } from './services/saving-workbaskets/saving-workbaskets.service';
import { OrientationService } from './services/orientation/orientation.service';
import { ClassificationDefinitionService } from './services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from './services/workbasket-definition/workbasket-definition.service';
import { SelectedRouteService } from './services/selected-route/selected-route';
import { ClassificationsService } from './services/classifications/classifications.service';
import { TreeService } from './services/tree/tree.service';
import { ClassificationTypesService } from './services/classification-types/classification-types.service';
import { ClassificationCategoriesService } from 'app/services/classification-categories-service/classification-categories.service';
import { StartupService } from 'app/services/startup-service/startup.service';


/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/removeNoneType/remove-none-type.pipe';
import { SelectWorkBasketPipe } from './pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { SpreadNumberPipe } from './pipes/spreadNumber/spread-number';
import { DomainService } from './services/domain/domain.service';

/**
 * Guards
 */
import { DomainGuard } from './guards/domain-guard';

const MODULES = [
  BrowserModule,
  FormsModule,
  TabsModule.forRoot(),
  TreeModule,
  AppRoutingModule,
  AlertModule.forRoot(),
  AngularSvgIconModule,
  HttpClientModule,
  BrowserAnimationsModule,
  ReactiveFormsModule
];

const DECLARATIONS = [
  AppComponent,
  WorkbasketListComponent,
  WorkbasketListToolbarComponent,
  AccessItemsComponent,
  WorkbasketDetailsComponent,
  MasterAndDetailComponent,
  WorkbasketInformationComponent,
  NoAccessComponent,
  SpinnerComponent,
  FilterComponent,
  IconTypeComponent,
  AlertComponent,
  GeneralMessageModalComponent,
  DistributionTargetsComponent,
  SortComponent,
  DualListComponent,
  PaginationComponent,
  ClassificationListComponent,
  ImportExportComponent,
  TaskanaTreeComponent,
  ClassificationTypesSelectorComponent,
  ClassificationDetailsComponent,
  NavBarComponent,
  MapValuesPipe,
  RemoveNoneTypePipe,
  SelectWorkBasketPipe,
  SpreadNumberPipe
];

export function startupServiceFactory(startupService: StartupService): Function {
  return () => startupService.load();
}

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WorkbasketService,
    MasterAndDetailService,
    PermissionService,
    ClassificationDefinitionService,
    WorkbasketDefinitionService,
    DomainService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpClientInterceptor,
      multi: true
    },
    AlertService,
    ErrorModalService,
    RequestInProgressService,
    SavingWorkbasketService,
    OrientationService,
    SelectedRouteService,
    ClassificationsService,
    TreeService,
    ClassificationTypesService,
    ClassificationCategoriesService,
    DomainGuard,
    StartupService,
    {
      provide: APP_INITIALIZER,
      useFactory: startupServiceFactory,
      deps: [StartupService],
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

// tslint:enable:max-line-length
