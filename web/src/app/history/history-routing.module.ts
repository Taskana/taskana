import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TaskHistoryQueryComponent } from './task-history-query/task-history-query.component';

const routes: Routes = [
  {
    path: '',
    component: TaskHistoryQueryComponent
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
export class HistoryRoutingModule {}
