import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TaskFacadeService } from '@task/services/task-facade.service';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'taskana-task-details-container',
  templateUrl: './task-details-container.component.html',
  styleUrls: ['./task-details-container.component.scss']
})
export class TaskDetailsContainerComponent implements OnInit {
  constructor(private route: ActivatedRoute, private taskFacade: TaskFacadeService) {}

  ngOnInit(): void {
    //TODO add takeuntil destroy, can use https://github.com/ngneat/until-destroy
    this.route.params.pipe().subscribe((param) => {
      const taskId = param.id;
      this.taskFacade.getTask(taskId);
    });
  }
}
