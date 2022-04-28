import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TaskRoutingModule } from './task-routing.module';
import { TaskListComponent } from './components/task-list/task-list.component';
import { SharedModule } from '@shared/shared.module';
import { TaskOverviewComponent } from './components/task-overview/task-overview.component';
import { TaskDetailsComponent } from './components/task-details/task-details.component';
import { TaskContainerComponent } from './components/task-container/task-container.component';
import { NgxsModule } from '@ngxs/store';
import { TaskState } from './store/task.state';
import { TaskDetailsContainerComponent } from './components/task-details-container/task-details-container.component';

@NgModule({
  declarations: [
    TaskListComponent,
    TaskOverviewComponent,
    TaskDetailsComponent,
    TaskContainerComponent,
    TaskDetailsContainerComponent
  ],
  imports: [CommonModule, TaskRoutingModule, SharedModule, NgxsModule.forFeature([TaskState])]
})
export class TaskModule {}
