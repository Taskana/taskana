import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { AlertModule, TypeaheadModule } from 'ngx-bootstrap';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { SharedModule } from 'app/shared/shared.module';
import { ClassificationCategoriesService } from 'app/shared/services/classification-categories/classification-categories.service';
import { WorkplaceRoutingModule } from './workplace-routing.module';

import { TaskListToolbarComponent } from './taskmaster/task-list-toolbar/task-list-toolbar.component';
import { TaskMasterComponent } from './taskmaster/task-master.component';
import { TaskdetailsComponent } from './taskdetails/taskdetails.component';
import { TaskdetailsGeneralFieldsComponent } from './taskdetails/general/general-fields.component';
import { TaskdetailsCustomFieldsComponent } from './taskdetails/custom/custom-fields.component';
import { TaskdetailsAttributeComponent } from './taskdetails/attribute/attribute.component';
import { TaskComponent } from './task/task.component';
import { CodeComponent } from './components/code/code.component';
import { GeneralFieldsExtensionComponent } from './taskdetails/general-fields-extension/general-fields-extension.component';
import { TaskListComponent } from './taskmaster/task-list/task-list.component';

import { OrderTasksByPipe } from './util/orderTasksBy.pipe';

import { TaskService } from './services/task.service';
import { CustomHttpClientInterceptor } from './services/custom-http-interceptor/custom-http-interceptor.service';
import { WorkplaceService } from './services/workplace.service';

const MODULES = [
  TypeaheadModule.forRoot(),
  AccordionModule.forRoot(),
  BsDropdownModule.forRoot(),
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
  TaskMasterComponent,
  TaskdetailsComponent,
  TaskdetailsGeneralFieldsComponent,
  TaskdetailsCustomFieldsComponent,
  TaskdetailsAttributeComponent,
  TaskComponent,
  CodeComponent,
  GeneralFieldsExtensionComponent,
  TaskListComponent,
  OrderTasksByPipe
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    TaskService,
    ClassificationCategoriesService,
    WorkplaceService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CustomHttpClientInterceptor,
      multi: true
    },
  ]
})
export class WorkplaceModule {
}
