import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap';
import { ChartsModule } from 'ng2-charts';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { HttpClientModule } from '@angular/common/http';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { MonitorRoutingModule } from './monitor-routing.module';
import { SharedModule } from 'app/shared/shared.module';

import { TasksComponent } from './tasks/tasks.component';
import { WorkbasketComponent } from './workbasket/workbasket.component';
import { ReportComponent } from './report/report.component';
import { MonitorComponent } from './monitor.component';

import { RestConnectorService } from './services/restConnector/rest-connector.service';

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
]
const DECLARATIONS = [
  TasksComponent,
  WorkbasketComponent,
  ReportComponent,
  MonitorComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: [RestConnectorService]
})
export class MonitorModule {
}
