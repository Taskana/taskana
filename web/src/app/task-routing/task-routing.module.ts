import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TaskRoutingRoutingModule } from './task-routing-routing.module';
import { RoutingUploadComponent } from './routing-upload/routing-upload.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [RoutingUploadComponent],
  imports: [CommonModule, TaskRoutingRoutingModule, SharedModule]
})
export class TaskRoutingModule {}
