import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Ng2AutoCompleteModule} from 'ng2-auto-complete';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {WorkplaceRoutingModule} from './workplace-routing.module';
import {AlertModule} from 'ngx-bootstrap';

import { SelectorComponent } from './workbasket-selector/workbasket-selector.component';
import { TasklistComponent } from './tasklist/tasklist.component';
import { TaskdetailsComponent } from './taskdetails/taskdetails.component';
import { TaskComponent } from './task/task.component';
import { CodeComponent } from './components/code/code.component';


import { OrderTasksByPipe } from './util/orderTasksBy.pipe';

import {TaskService} from './services/task.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {SharedModule} from '../shared/shared.module';
import {CustomHttpClientInterceptor} from './services/custom-http-interceptor/custom-http-interceptor.service';


const MODULES = [
  CommonModule,
  FormsModule,
  Ng2AutoCompleteModule,
  HttpClientModule,
  AngularSvgIconModule,
  WorkplaceRoutingModule,
  AlertModule,
  SharedModule
];

const DECLARATIONS = [
  SelectorComponent,
  TasklistComponent,
  TaskdetailsComponent,
  TaskComponent,
  CodeComponent,
  OrderTasksByPipe
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    TaskService,
    WorkbasketService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CustomHttpClientInterceptor,
      multi: true
    },
  ]
})
export class WorkplaceModule {
}
