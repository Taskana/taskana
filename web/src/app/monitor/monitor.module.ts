import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AlertModule} from 'ngx-bootstrap';
import {ChartsModule} from 'ng2-charts';
import {TabsModule} from 'ngx-bootstrap/tabs';
import {HttpClientModule} from '@angular/common/http';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {MonitorRoutingModule} from './monitor-routing.module';
import {SharedModule} from '../shared/shared.module';

import {ReportComponent} from './report/report.component';
import {MonitorComponent} from './monitor.component';
import {TasksComponent} from './tasks/tasks.component';
import {WorkbasketComponent} from './workbasket/workbasket.component';
import {ClassificationTasksComponent} from './classification-tasks/classification-tasks.component';
import {ReportRowComponent} from './report/row/row.component';
import {TimestampComponent} from './timestamp/timestamp.component';

import {RestConnectorService} from './services/restConnector/rest-connector.service';

import {MapToIterable} from '../shared/pipes/mapToIterable/mapToIterable';

const MODULES = [
  CommonModule,
  MonitorRoutingModule,
  FormsModule,
  AlertModule.forRoot(),
  ChartsModule,
  TabsModule.forRoot(),
  HttpClientModule,
  AngularSvgIconModule,
  SharedModule
];
const DECLARATIONS = [
  TasksComponent,
  WorkbasketComponent,
  ReportComponent,
  MonitorComponent,
  ClassificationTasksComponent,
  TimestampComponent,
  ReportRowComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [RestConnectorService, MapToIterable]
})
export class MonitorModule {
}
