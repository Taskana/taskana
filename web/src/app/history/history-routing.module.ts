import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TaskQueryComponent } from './task-query/task-query.component';

const routes: Routes = [
  {
    path: '',
    component: TaskQueryComponent
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
