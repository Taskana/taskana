import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoutingUploadComponent } from './routing-upload/routing-upload.component';
import { TaskRoutingGuard } from '../shared/guards/task-routing.guard';

const routes: Routes = [
  {
    path: '',
    canActivate: [TaskRoutingGuard],
    component: RoutingUploadComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TaskRoutingRoutingModule {}
