import { Component, OnInit } from '@angular/core';
import { TaskFacadeService } from '@task/services/task-facade.service';

@Component({
  selector: 'taskana-task-container',
  templateUrl: './task-container.component.html',
  styleUrls: ['./task-container.component.scss']
})
export class TaskContainerComponent implements OnInit {
  constructor(private taskFacade: TaskFacadeService) {}

  ngOnInit(): void {
    this.taskFacade.getTasks();
  }
}
