/**
 * Modules
 */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule, } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
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
import { WorkbasketListComponent } from './workbasket/list/workbasket-list.component';
import { WorkbasketListToolbarComponent } from './workbasket/list/workbasket-list-toolbar/workbasket-list-toolbar.component'
import { WorkbasketDetailsComponent } from './workbasket/details/workbasket-details.component';
import { WorkbasketInformationComponent } from './workbasket/details/information/workbasket-information.component';
import { DistributionTargetsComponent } from './workbasket/details/distribution-targets/distribution-targets.component';
import { DualListComponent } from './workbasket/details/distribution-targets/dual-list/dual-list.component';
import { AccessItemsComponent } from './workbasket/details/access-items/access-items.component';
import { NoAccessComponent } from './workbasket/noAccess/no-access.component';
import { SpinnerComponent } from './shared/spinner/spinner.component';
import { FilterComponent } from './shared/filter/filter.component';
import { IconTypeComponent } from './shared/type-icon/icon-type.component';
import { AlertComponent } from './shared/alert/alert.component';
import { SortComponent } from './shared/sort/sort.component';
import { GeneralMessageModalComponent } from './shared/general-message-modal/general-message-modal.component';

// Shared
import { MasterAndDetailComponent } from './shared/masterAndDetail/master-and-detail.component';

/**
 * Services
 */
import { WorkbasketService } from './services/workbasket.service';
import { MasterAndDetailService } from './services/master-and-detail.service';
import { HttpClientInterceptor } from './services/http-client-interceptor.service';
import { PermissionService } from './services/permission.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AlertService } from './services/alert.service';
import { ErrorModalService } from './services/error-modal.service';
import { RequestInProgressService } from './services/request-in-progress.service';
import { SavingWorkbasketService } from './services/saving-workbaskets/saving-workbaskets.service';



/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/remove-none-type';
import { SelectWorkBasketPipe } from './pipes/seleted-workbasket.pipe';
import {WorkbasketDefinitionService} from './services/workbasketDefinition/workbasketDefinition.service';
import {ClassificationService} from './services/classification.service';
import {ImportExportComponent} from './import-export/import-export.component';
import {ClassificationListComponent} from './classification/ist/classification-list.component';

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
  MapValuesPipe,
  RemoveNoneTypePipe,
  SelectWorkBasketPipe,
  ClassificationListComponent,
  ImportExportComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    WorkbasketService,
    MasterAndDetailService,
    PermissionService,
    ClassificationService,
    WorkbasketDefinitionService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpClientInterceptor,
      multi: true
    },
    AlertService,
    ErrorModalService,
    RequestInProgressService,
    SavingWorkbasketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
