import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SharedModule } from 'app/shared/shared.module';
import { HistoryRoutingModule } from './history-routing.module';
import { TaskQueryComponent } from './task-query/task-query.component';

@NgModule({
  imports: [
    CommonModule,
    HistoryRoutingModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [TaskQueryComponent]
})
export class HistoryModule { }
