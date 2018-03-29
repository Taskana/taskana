import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Ng2AutoCompleteModule} from 'ng2-auto-complete';
import {HttpClientModule} from '@angular/common/http';
import {AngularSvgIconModule} from 'angular-svg-icon';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {AlertModule} from 'ngx-bootstrap';
import {SelectorComponent} from './workbasket-selector/workbasket-selector.component';
import {TasklistComponent} from './tasklist/tasklist.component';
import {TaskdetailsComponent} from './taskdetails/taskdetails.component';

import {OrderTasksByPipe} from './util/orderTasksBy.pipe';

import {TaskComponent} from './task/task.component';
import {TasksComponent} from './tasks/tasks.component';
import {TaskService} from './services/task.service';
import {WorkbasketService} from './services/workbasket.service';


@NgModule({
  declarations: [
    AppComponent,
    SelectorComponent,
    TasklistComponent,
    TaskdetailsComponent,
    OrderTasksByPipe,
    TaskComponent,
    TasksComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AlertModule.forRoot(),
    Ng2AutoCompleteModule,
    AppRoutingModule,
    HttpClientModule,
    AngularSvgIconModule
  ],
  providers: [
    HttpClientModule,
    TaskService,
    WorkbasketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
