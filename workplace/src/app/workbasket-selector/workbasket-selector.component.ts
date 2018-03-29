import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {DataService} from '../services/data.service';
import {Task} from '../model/task';
import {Workbasket} from '../model/workbasket';
import {TaskService} from '../services/task.service';
import {WorkbasketService} from '../services/workbasket.service';

@Component({
  selector: 'workbasket-selector',
  templateUrl: './workbasket-selector.component.html',
  styleUrls: ['./workbasket-selector.component.scss']
})
export class SelectorComponent implements OnInit {

  @Output('tasks') tasksEmitter = new EventEmitter<Task[]>();

  tasks: Task[] = [];

  autoCompleteData: string[] = [];
  result: string;
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
      this.tasksEmitter.emit(this.tasks);
    }
  }

  getTasks(workbasketKey: string) {
    this.taskService.findTaskWithWorkbaskets(workbasketKey).subscribe(
      tasks2 => {
        tasks2['_embedded']['tasks'].forEach(e => this.tasks.push(e));
      });
  }
}
