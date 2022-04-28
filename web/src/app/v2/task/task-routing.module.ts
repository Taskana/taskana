import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaskContainerComponent } from './components/task-container/task-container.component';
import { TaskDetailsContainerComponent } from './components/task-details-container/task-details-container.component';

const routes: Routes = [
  {
    path: 'tasks',
    component: TaskContainerComponent,
    children: [
      {
        path: 'taskdetail/:id',
        component: TaskDetailsContainerComponent
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
export class TaskRoutingModule {}
