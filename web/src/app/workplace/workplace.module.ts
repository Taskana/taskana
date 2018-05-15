import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Ng2AutoCompleteModule } from 'ng2-auto-complete';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { WorkplaceRoutingModule } from './workplace-routing.module';
import { AlertModule } from 'ngx-bootstrap';
import { SharedModule } from 'app/shared/shared.module';

import { WorkplaceComponent } from './workplace.component';
import { SelectorComponent } from './workbasket-selector/workbasket-selector.component';
import { TasklistComponent } from './tasklist/tasklist.component';
import { TaskdetailsComponent } from './taskdetails/taskdetails.component';
import { TaskComponent } from './task/task.component';
import { TasksComponent } from './tasks/tasks.component';
import { CodeComponent } from './components/code/code.component';


import { OrderTasksByPipe } from './util/orderTasksBy.pipe';

import { DataService } from './services/data.service';
import { TaskService } from './services/task.service';
import { WorkbasketService } from './services/workbasket.service';
import { CustomHttpClientInterceptor } from './services/custom-http-interceptor/custom-http-interceptor.service';


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
  WorkplaceComponent,
  SelectorComponent,
  TasklistComponent,
  TaskdetailsComponent,
  TaskComponent,
  TasksComponent,
  CodeComponent,
  OrderTasksByPipe
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [
    DataService,
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
