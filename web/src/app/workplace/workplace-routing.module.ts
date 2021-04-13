import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MasterAndDetailComponent } from '../shared/components/master-and-detail/master-and-detail.component';
import { TaskProcessingComponent } from './components/task-processing/task-processing.component';
import { TaskdetailsComponent } from './components/taskdetails/taskdetails.component';
import { TaskMasterComponent } from './components/task-master/task-master.component';

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
        component: TaskProcessingComponent,
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
