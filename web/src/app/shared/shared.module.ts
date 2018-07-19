import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule } from 'ngx-bootstrap';
import { RouterModule } from '@angular/router';
import { TreeModule } from 'angular-tree-component';
import { TypeaheadModule } from 'ngx-bootstrap';

/**
 * Components
 */
import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { AlertComponent } from 'app/shared/alert/alert.component';
import { MasterAndDetailComponent } from 'app/shared/master-and-detail/master-and-detail.component';
import { TaskanaTreeComponent } from 'app/shared/tree/tree.component';
import { TypeAheadComponent } from 'app/shared/type-ahead/type-ahead.component';
import { SortComponent } from './sort/sort.component';
import { RemoveConfirmationComponent } from 'app/shared/remove-confirmation/remove-confirmation.component';
import { FilterComponent } from 'app/shared/filter/filter.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { FieldErrorDisplayComponent } from 'app/shared/field-error-display/field-error-display.component';

/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/removeNoneType/remove-none-type.pipe';
import { SelectWorkBasketPipe } from './pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { SpreadNumberPipe } from './pipes/spreadNumber/spread-number';
import { OrderBy } from './pipes/orderBy/orderBy';
import { MapToIterable } from './pipes/mapToIterable/mapToIterable';

/**
 * Services
 */
import { HttpClientInterceptor } from './services/httpClientInterceptor/http-client-interceptor.service';
import { AccessIdsService } from './services/access-ids/access-ids.service';

const MODULES = [
  CommonModule,
  FormsModule,
  AlertModule.forRoot(),
  TypeaheadModule.forRoot(),
  AngularSvgIconModule,
  HttpClientModule,
  RouterModule,
  TreeModule
];

const DECLARATIONS = [
  GeneralMessageModalComponent,
  SpinnerComponent,
  AlertComponent,
  MasterAndDetailComponent,
  TaskanaTreeComponent,
  TypeAheadComponent,
  MapValuesPipe,
  RemoveNoneTypePipe,
  SelectWorkBasketPipe,
  SpreadNumberPipe,
  OrderBy,
  MapToIterable,
  SortComponent,
  FilterComponent,
  IconTypeComponent,
  RemoveConfirmationComponent,
  FieldErrorDisplayComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  exports: DECLARATIONS,
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpClientInterceptor,
      multi: true
    },
    AccessIdsService,
  ]
})
export class SharedModule {
}
