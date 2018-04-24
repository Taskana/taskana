import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule } from 'ngx-bootstrap';
import { RouterModule } from '@angular/router';
import { TreeModule } from 'angular-tree-component';

import { GeneralMessageModalComponent } from 'app/shared/general-message-modal/general-message-modal.component';
import { SpinnerComponent } from 'app/shared/spinner/spinner.component';
import { AlertComponent } from 'app/shared/alert/alert.component';
import { MasterAndDetailComponent } from 'app/shared/master-and-detail/master-and-detail.component';
import { TaskanaTreeComponent } from 'app/shared/tree/tree.component';

/**
 * Pipes
 */
import { MapValuesPipe } from './pipes/mapValues/map-values.pipe';
import { RemoveNoneTypePipe } from './pipes/removeNoneType/remove-none-type.pipe';
import { SelectWorkBasketPipe } from './pipes/selectedWorkbasket/seleted-workbasket.pipe';
import { SpreadNumberPipe } from './pipes/spreadNumber/spread-number';
import { OrderBy } from './pipes/orderBy/orderBy';
import { MapToIterable } from './pipes/mapToIterable/mapToIterable';

const MODULES = [
  CommonModule,
  FormsModule,
  AlertModule.forRoot(),
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
  MapValuesPipe,
  RemoveNoneTypePipe,
  SelectWorkBasketPipe,
  SpreadNumberPipe,
  OrderBy,
  MapToIterable
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  exports: DECLARATIONS
})
export class SharedModule {
}
