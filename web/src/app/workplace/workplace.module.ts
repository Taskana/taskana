import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {WorkplaceRoutingModule} from './workplace-routing.module';
import {AlertModule, TypeaheadModule} from 'ngx-bootstrap';

import {TaskListToolbarComponent} from './tasklist/tasklist-toolbar/tasklist-toolbar.component';
import {TasklistComponent} from './tasklist/tasklist.component';
import {TaskdetailsComponent} from './taskdetails/taskdetails.component';
import {TaskdetailsGeneralFieldsComponent} from './taskdetails/general/general-fields.component';
import {TaskdetailsCustomFieldsComponent} from './taskdetails/custom/custom-fields.component';
import {TaskdetailsAttributeComponent} from './taskdetails/attribute/attribute.component';
import {TaskComponent} from './task/task.component';
import {CodeComponent} from './components/code/code.component';


import {OrderTasksByPipe} from './util/orderTasksBy.pipe';

import {TaskService} from './services/task.service';
import {ClassificationsService} from 'app/services/classifications/classifications.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {SharedModule} from 'app/shared/shared.module';
import {CustomHttpClientInterceptor} from './services/custom-http-interceptor/custom-http-interceptor.service';
import {ClassificationCategoriesService} from 'app/services/classifications/classification-categories.service';
import {WorkplaceService} from './services/workplace.service';
import {DatePipe} from '../shared/pipes/date/date-pipe';


const MODULES = [
  TypeaheadModule.forRoot(),
  CommonModule,
  FormsModule,
  HttpClientModule,
  AngularSvgIconModule,
  WorkplaceRoutingModule,
  AlertModule,
  SharedModule
];

const DECLARATIONS = [
  TaskListToolbarComponent,
  TasklistComponent,
  TaskdetailsComponent,
  TaskdetailsGeneralFieldsComponent,
  TaskdetailsCustomFieldsComponent,
  TaskdetailsAttributeComponent,
  TaskComponent,
  CodeComponent,
  OrderTasksByPipe,
  DatePipe
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    TaskService,
    ClassificationsService,
    ClassificationCategoriesService,
    WorkplaceService,
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
