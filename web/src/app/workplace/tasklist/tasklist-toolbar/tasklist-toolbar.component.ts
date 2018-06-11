import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {Workbasket} from 'app/models/workbasket';
import {TaskService} from 'app/workplace/services/task.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {SortingModel} from 'app/models/sorting';

@Component({
  selector: 'taskana-tasklist-toolbar',
  templateUrl: './tasklist-toolbar.component.html'
})
export class TaskListToolbarComponent implements OnInit {

  @Output() tasksChanged = new EventEmitter<Task[]>();
  @Output() basketChanged = new EventEmitter<Workbasket>();
  @Output() performSorting = new EventEmitter<SortingModel>();

  sortingFields = new Map([['name', 'Name'], ['priority', 'Priority'], ['due', 'Due'], ['planned', 'Planned']]);
  tasks: Task[] = [];

  workbasketNames: string[] = [];
  result = '';
  resultId = '';
  workbaskets: Workbasket[];
  currentBasket: Workbasket;
  workbasketSelected = false;

  constructor(private taskService: TaskService,
              private workbasketService: WorkbasketService) {
  }

  ngOnInit() {
    this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
      this.workbaskets = workbaskets._embedded ? workbaskets._embedded.workbaskets : [];
      this.workbaskets.forEach(workbasket => {
        this.workbasketNames.push(workbasket.name);
      });
    });
  }

  searchBasket() {
    if (this.workbaskets) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.result) {
          this.resultId = workbasket.workbasketId;
          this.currentBasket = workbasket;
        }
      });

      if (this.resultId.length > 0) {
        this.getTasks(this.resultId);
      } else {
        this.tasks = [];
        this.currentBasket = null;
        this.tasksChanged.emit(this.tasks);
      }
      this.basketChanged.emit(this.currentBasket);
    }
    this.resultId = '';
  }

  getTasks(workbasketId: string) {
    this.taskService.findTasksWithWorkbasket(workbasketId).subscribe(
      tasks => {
        this.tasks.length = 0;
        if (!tasks || tasks._embedded === undefined) {
          return;
        }
        this.tasks = tasks._embedded.tasks;
        this.tasksChanged.emit(this.tasks);
      });
  }

  sorting(sort: SortingModel) {
    this.performSorting.emit(sort);
  }
}
