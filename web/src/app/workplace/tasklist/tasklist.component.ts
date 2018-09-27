import {Component, OnDestroy, OnInit} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskService} from 'app/workplace/services/task.service';
import {Subscription} from 'rxjs';
import {SortingModel} from 'app/models/sorting';
import {Workbasket} from 'app/models/workbasket';
import {FilterModel} from 'app/models/filter';
import {AlertService} from 'app/services/alert/alert.service';
import {AlertModel, AlertType} from 'app/models/alert';
import {WorkplaceService} from 'app/workplace/services/workplace.service';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit, OnDestroy {

  tasks: Task[];

  currentBasket: Workbasket;
  selectedId = '';
  sort: SortingModel = new SortingModel('priority');
  filterBy: FilterModel = new FilterModel({
    name: '',
    owner: '',
    priority: '',
    state: '',
    classificationKey: '',
    workbasketId: '',
    workbasketKey: ''
  });
  requestInProgress = false;

  private taskChangeSubscription: Subscription;
  private taskDeletedSubscription: Subscription;
  private taskAddedSubscription: Subscription;
  private workbasketChangeSubscription: Subscription;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private taskService: TaskService,
              private workplaceService: WorkplaceService,
              private alertService: AlertService) {
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
    });
    this.workbasketChangeSubscription = this.workplaceService.workbasketSelectedStream.subscribe(workbasket => {
      this.currentBasket = workbasket;
      this.getTasks();
    });
    this.taskAddedSubscription = this.taskService.taskAddedStream.subscribe(task => {
      this.getTasks();
      this.selectedId = task.taskId;
    });
  }

  ngOnInit() {
    this.taskService.getSelectedTask().subscribe(
      task => {
        if (!this.currentBasket) {
          this.selectedId = task.taskId;
          this.currentBasket = task.workbasketSummaryResource;
          this.getTasks();
        }
        if (!task) {
          this.selectedId = undefined;
        }
      });
  }

  selectTask(taskId: string) {
    this.selectedId = taskId;
    this.router.navigate([{outlets: {detail: `taskdetail/${this.selectedId}`}}], {relativeTo: this.route});
  }

  performSorting(sort: SortingModel) {
    this.sort = sort;
    this.getTasks();
  }

  performFilter(filterBy: FilterModel) {
    this.filterBy = filterBy;
    this.getTasks();
  }

  getTasks(): void {
    this.requestInProgress = true;
    if (this.currentBasket === undefined) {
      this.requestInProgress = false;
      this.tasks = [];
    } else {
      this.taskService.findTasksWithWorkbasket(this.currentBasket.workbasketId, this.sort.sortBy, this.sort.sortDirection,
        this.filterBy.filterParams.name, this.filterBy.filterParams.owner, this.filterBy.filterParams.priority,
        this.filterBy.filterParams.state)
        .subscribe(tasks => {
          this.requestInProgress = false;
          if (tasks._embedded) {
            this.tasks = tasks._embedded.tasks;
          } else {
            this.tasks = [];
            this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'The selected Workbasket is empty!'));
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.taskChangeSubscription.unsubscribe();
    this.taskDeletedSubscription.unsubscribe();
    this.workbasketChangeSubscription.unsubscribe();
  }
}
