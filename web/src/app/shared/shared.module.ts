import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RouterModule } from '@angular/router';
import { AlertModule, TypeaheadModule, BsDatepickerModule } from 'ngx-bootstrap';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';


/**
 * Components
 */
import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { AlertComponent } from 'app/shared/alert/alert.component';
import { MasterAndDetailComponent } from 'app/shared/master-and-detail/master-and-detail.component';
import { TypeAheadComponent } from 'app/shared/type-ahead/type-ahead.component';
import { RemoveConfirmationComponent } from 'app/shared/remove-confirmation/remove-confirmation.component';
import { FilterComponent } from 'app/shared/filter/filter.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { FieldErrorDisplayComponent } from 'app/shared/field-error-display/field-error-display.component';
import { ErrorModalComponent } from './error-message-modal/error-modal.component';
import { SortComponent } from './sort/sort.component';
import { PaginationComponent } from './pagination/pagination.component';
import { NumberPickerComponent } from './number-picker/number-picker.component';
import { ProgressBarComponent } from './progress-bar/progress-bar.component';
import { DatePickerComponent } from './date-picker/date-picker.component';
import { DropdownComponent } from './dropdown/dropdown.component';

/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/removeNoneType/remove-none-type.pipe';
import { SelectWorkBasketPipe } from './pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { SpreadNumberPipe } from './pipes/spreadNumber/spread-number';
import { OrderBy } from './pipes/orderBy/orderBy';
import { MapToIterable } from './pipes/mapToIterable/mapToIterable';
import { NumberToArray } from './pipes/numberToArray/numberToArray';
import { DateTimeZonePipe } from './pipes/date-time-zone/date-time-zone.pipe';


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
  AccordionModule.forRoot(),
  BsDatepickerModule.forRoot(),
  AngularSvgIconModule,
  HttpClientModule,
  RouterModule,
];

const DECLARATIONS = [
  GeneralMessageModalComponent,
  ErrorModalComponent,
  SpinnerComponent,
  AlertComponent,
  MasterAndDetailComponent,
  TypeAheadComponent,
  MapValuesPipe,
  RemoveNoneTypePipe,
  SelectWorkBasketPipe,
  SpreadNumberPipe,
  DateTimeZonePipe,
  NumberToArray,
  OrderBy,
  MapToIterable,
  SortComponent,
  FilterComponent,
  IconTypeComponent,
  RemoveConfirmationComponent,
  FieldErrorDisplayComponent,
  PaginationComponent,
  NumberPickerComponent,
  ProgressBarComponent,
  DatePickerComponent,
  DropdownComponent
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
    ClassificationsService,
    WorkbasketService
  ]
})
export class SharedModule {
}
