import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RouterModule } from '@angular/router';
import { TreeModule } from 'angular-tree-component';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';

import { AccordionModule } from 'ngx-bootstrap/accordion';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';

/**
 * Components
 */
import { SpinnerComponent } from 'app/shared/components/spinner/spinner.component';
import { MasterAndDetailComponent } from 'app/shared/components/master-and-detail/master-and-detail.component';
import { TaskanaTreeComponent } from 'app/administration/components/tree/tree.component';
import { TypeAheadComponent } from 'app/shared/components/type-ahead/type-ahead.component';
import { FilterComponent } from 'app/shared/components/filter/filter.component';
import { IconTypeComponent } from 'app/administration/components/type-icon/icon-type.component';
import { FieldErrorDisplayComponent } from 'app/shared/components/field-error-display/field-error-display.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { SortComponent } from './components/sort/sort.component';
import { PaginationComponent } from './components/pagination/pagination.component';
import { NumberPickerComponent } from './components/number-picker/number-picker.component';
import { ProgressBarComponent } from './components/progress-bar/progress-bar.component';
import { DatePickerComponent } from './components/date-picker/date-picker.component';
import { DropdownComponent } from './components/dropdown/dropdown.component';

/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/remove-empty-type.pipe';
import { SelectWorkBasketPipe } from './pipes/select-workbaskets.pipe';
import { SpreadNumberPipe } from './pipes/spread-number.pipe';
import { OrderBy } from './pipes/order-by.pipe';
import { MapToIterable } from './pipes/map-to-iterable.pipe';
import { NumberToArray } from './pipes/number-to-array.pipe';
import { DateTimeZonePipe } from './pipes/date-time-zone.pipe';

/**
 * Services
 */
import { HttpClientInterceptor } from './services/http-client-interceptor/http-client-interceptor.service';
import { AccessIdsService } from './services/access-ids/access-ids.service';
import { ToastComponent } from './components/toast/toast.component';
import { DialogPopUpComponent } from './components/popup/dialog-pop-up.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';

const MODULES = [
  CommonModule,
  FormsModule,
  AlertModule.forRoot(),
  TypeaheadModule.forRoot(),
  AccordionModule.forRoot(),
  BsDatepickerModule.forRoot(),
  AngularSvgIconModule,
  HttpClientModule,
  MatSnackBarModule,
  MatDialogModule,
  MatButtonModule,
  RouterModule,
  TreeModule.forRoot()
];

const DECLARATIONS = [
  SpinnerComponent,
  MasterAndDetailComponent,
  TaskanaTreeComponent,
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
  FieldErrorDisplayComponent,
  PaginationComponent,
  NumberPickerComponent,
  ProgressBarComponent,
  DatePickerComponent,
  DropdownComponent,
  ToastComponent,
  DialogPopUpComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [
    MODULES,
    MatRadioModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule
  ],
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
  ],
  entryComponents: [ToastComponent, DialogPopUpComponent]
})
export class SharedModule {}
