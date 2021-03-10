import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { AlertModule } from 'ngx-bootstrap/alert';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ChartsModule } from 'ng2-charts';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { MapToIterable } from 'app/shared/pipes/map-to-iterable.pipe';
import { SharedModule } from '../shared/shared.module';
import { MonitorRoutingModule } from './monitor-routing.module';

import { ReportTableComponent } from './components/report-table/report-table.component';
import { MonitorComponent } from './components/monitor/monitor.component';
import { TaskReportComponent } from './components/task-report/task-report.component';
import { ClassificationReportComponent } from './components/classification-report/classification-report.component';
import { TimestampReportComponent } from './components/timestamp-report/timestamp-report.component';
import { WorkbasketReportComponent } from './components/workbasket-report/workbasket-report.component';
import { WorkbasketReportPlannedDateComponent } from './components/workbasket-report-planned-date/workbasket-report-planned-date.component';
import { WorkbasketReportDueDateComponent } from './components/workbasket-report-due-date/workbasket-report-due-date.component';
import { MonitorService } from './services/monitor.service';

const MODULES = [
  CommonModule,
  MonitorRoutingModule,
  FormsModule,
  AlertModule.forRoot(),
  ChartsModule,
  TabsModule.forRoot(),
  HttpClientModule,
  AngularSvgIconModule,
  SharedModule,
  MatTabsModule
];
const DECLARATIONS = [
  ReportTableComponent,
  MonitorComponent,
  TimestampReportComponent,
  WorkbasketReportComponent,
  WorkbasketReportPlannedDateComponent,
  WorkbasketReportDueDateComponent,
  TaskReportComponent,
  ClassificationReportComponent
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [MODULES, MatButtonModule],
  providers: [MonitorService, MapToIterable]
})
export class MonitorModule {}
