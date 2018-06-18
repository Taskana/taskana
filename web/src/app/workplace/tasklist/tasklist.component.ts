import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from 'app/workplace/services/task.service';
import {Subscription} from 'rxjs/Subscription';
import {SortingModel} from 'app/models/sorting';
import {Workbasket} from 'app/models/workbasket';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit, OnDestroy {

  @Input() tasks: Task[];

  currentBasket: Workbasket;
  selectedId = '';
  sort: SortingModel = new SortingModel();
  requestInProgress = false;

  private taskChangeSubscription: Subscription;
  private taskDeletedSubscription: Subscription;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private taskService: TaskService) {
    this.taskChangeSubscription = this.taskService.taskChangedStream.subscribe(task => {
      for (let i = 0; i < this.tasks.length; i++) {
        if (this.tasks[i].taskId === task.taskId) {
          this.tasks[i] = task;
        }
      }
    });
    this.taskDeletedSubscription = this.taskService.taskDeletedStream.subscribe(task => {
      for (let i = 0; i < this.tasks.length; i++) {
        if (this.tasks[i].taskId === task.taskId) {
          this.tasks.splice(i, 1);
        }
      }
    })
  }

  ngOnInit() {
  }

  loadTasks(tasks: Task[]) {
    this.tasks = tasks;
  }

  loadBasketID(workbasket: Workbasket) {
    this.currentBasket = workbasket;
  }

  selectTask(taskId: string) {
    this.selectedId = taskId;
    this.router.navigate([{outlets: {detail: `taskdetail/${this.selectedId}`}}], {relativeTo: this.route});
  }

  performSorting(sort: SortingModel) {
    this.sort = sort;
    this.getTasks();
  }

  getTasks(): void {
    this.requestInProgress = true;
    this.taskService.findTasksWithWorkbasket(this.currentBasket.workbasketId, this.sort.sortBy, this.sort.sortDirection)
      .subscribe(tasks => {
        this.requestInProgress = false;
        this.tasks = tasks._embedded ? tasks._embedded.tasks : this.tasks;
      })
  }

  ngOnDestroy(): void {
    this.taskChangeSubscription.unsubscribe();
    this.taskDeletedSubscription.unsubscribe();
  }
}
