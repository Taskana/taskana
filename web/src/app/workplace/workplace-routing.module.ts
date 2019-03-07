import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MasterAndDetailComponent } from '../shared/master-and-detail/master-and-detail.component';
import { TaskComponent } from './task/task.component';
import { TaskdetailsComponent } from './taskdetails/taskdetails.component';
import { TaskMasterComponent } from './taskmaster/task-master.component';

const routes: Routes = [
  {
    path: 'tasks',
    component: MasterAndDetailComponent,
    children: [
      {
        path: '',
        component: TaskMasterComponent,
        outlet: 'master'
      },
      {
        path: 'taskdetail/:id',
        component: TaskdetailsComponent,
        outlet: 'detail'
      },
      {
        path: 'task/:id',
        component: TaskComponent,
        outlet: 'detail'
      }
    ]
  },
  {
    path: '',
    redirectTo: 'tasks',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'tasks'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WorkplaceRoutingModule {}
