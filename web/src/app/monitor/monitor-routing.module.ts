import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MonitorComponent } from './components/monitor/monitor.component';
import { TaskReportComponent } from './components/task-report/task-report.component';
import { WorkbasketReportComponent } from './components/workbasket-report/workbasket-report.component';
import { ClassificationReportComponent } from './components/classification-report/classification-report.component';
import { TimestampReportComponent } from './components/timestamp-report/timestamp-report.component';
import { TaskPriorityReportComponent } from './components/task-priority-report/task-priority-report.component';

const routes: Routes = [
  {
    path: '',
    component: MonitorComponent,
    children: [
      {
        path: 'tasks-priority',
        component: TaskPriorityReportComponent
      },
      {
        path: 'tasks-status',
        component: TaskReportComponent
      },
      {
        path: 'workbaskets',
        component: WorkbasketReportComponent
      },
      {
        path: 'classifications',
        component: ClassificationReportComponent
      },
      {
        path: 'timestamp',
        component: TimestampReportComponent
      }
    ]
  },
  {
    path: '',
    redirectTo: '',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MonitorRoutingModule {}
