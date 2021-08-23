import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoutingUploadComponent } from './routing-upload/routing-upload.component';

const routes: Routes = [
  {
    path: '',
    component: RoutingUploadComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TaskRoutingRoutingModule {}
