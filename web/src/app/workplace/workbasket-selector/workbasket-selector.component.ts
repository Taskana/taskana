import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DataService } from '../services/data.service';
import { Task } from '../models/task';
import { Workbasket } from 'app/models/workbasket';
import { TaskService } from '../services/task.service';
import { WorkbasketService } from '../services/workbasket.service';

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
    private workbasketService: WorkbasketService,
    private dataService: DataService) {
  }

  ngOnInit() {
    this.workbasketService.getAllWorkBaskets().subscribe(w => {
      this.workbaskets = w['_embedded']['workbaskets'];
      this.workbaskets.forEach(workbasket => {
        this.autoCompleteData.push(workbasket.name);
      });
    });
    if (this.dataService.workbasketKey) {
      this.getTasks(this.dataService.workbasketKey);
      this.result = this.dataService.workbasketName;
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
      this.dataService.workbasketKey = this.resultKey;
      this.dataService.workbasketName = this.result;
      this.tasksChanged.emit(this.tasks);
    }
  }

  getTasks(workbasketKey: string) {
    this.taskService.findTaskWithWorkbaskets(workbasketKey).subscribe(
      tasks2 => {
        tasks2['_embedded']['tasks'].forEach(e => this.tasks.push(e));
      });
  }
}
