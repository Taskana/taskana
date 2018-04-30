import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {Workbasket} from 'app/models/workbasket';
import {TaskService} from 'app/workplace/services/task.service';
import {WorkbasketService} from 'app/workplace/services/workbasket.service';

@Component({
  selector: 'taskana-workbasket-selector',
  templateUrl: './workbasket-selector.component.html'
})
export class SelectorComponent implements OnInit {

  @Output()
  tasksChanged = new EventEmitter<Task[]>();

  tasks: Task[] = [];

  autoCompleteData: string[] = [];
  result = '';
  resultKey: string;
  workbaskets: Workbasket[];

  constructor(private taskService: TaskService,
              private workbasketService: WorkbasketService) {
  }

  ngOnInit() {
    this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
      this.workbaskets = workbaskets._embedded ? workbaskets._embedded.workbaskets : [];
      this.workbaskets.forEach(workbasket => {
        this.autoCompleteData.push(workbasket.name);
      });
    });
    if (this.workbasketService.workbasketKey) {
      this.getTasks(this.workbasketService.workbasketKey);
      this.result = this.workbasketService.workbasketName;
    }
  }

  searchBasket() {
    if (this.workbaskets) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.result) {
          this.resultKey = workbasket.workbasketId;
        }
      });
      this.getTasks(this.resultKey);
      this.workbasketService.workbasketKey = this.resultKey;
      this.workbasketService.workbasketName = this.result;
      this.tasksChanged.emit(this.tasks);
    }
  }

  getTasks(workbasketKey: string) {
    this.taskService.findTasksWithWorkbasket(workbasketKey).subscribe(
      tasks => {
        if (!tasks || tasks._embedded === undefined) {
          this.tasks.length = 0;
          return;
        }
        tasks._embedded.tasks.forEach(e => this.tasks.push(e));
      });
  }
}
