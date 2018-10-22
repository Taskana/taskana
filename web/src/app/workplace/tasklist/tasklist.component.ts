import {Component, OnDestroy, OnInit, ViewChild, ElementRef} from '@angular/core';
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
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { Page } from 'app/models/page';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { Orientation } from 'app/models/orientation';

@Component({
  selector: 'taskana-task-list',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit, OnDestroy {

  tasks: Task[];

  page: Page;
  pageSelected = 1;
  pageSize = 7;
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

  @ViewChild('wbToolbar')
  private toolbarElement: ElementRef;
  private taskChangeSubscription: Subscription;
  private taskDeletedSubscription: Subscription;
  private taskAddedSubscription: Subscription;
  private workbasketChangeSubscription: Subscription;
  private orientationSubscription: Subscription;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private taskService: TaskService,
              private workplaceService: WorkplaceService,
              private alertService: AlertService,
              private orientationService: OrientationService) {
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
    TaskanaQueryParameters.page = this.pageSelected;
    TaskanaQueryParameters.pageSize = this.pageSize;
    this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
      this.refreshWorkbasketList();
    })
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

  changePage(page) {
    TaskanaQueryParameters.page = page;
    this.getTasks();
  }

  refreshWorkbasketList() {
    const toolbarSize = this.toolbarElement.nativeElement.offsetHeight;
    const cardHeight = 95;
    const unusedHeight = 145;
    const totalHeight = window.innerHeight;
    const cards = Math.round((totalHeight - (unusedHeight + toolbarSize)) / cardHeight);
    TaskanaQueryParameters.pageSize = cards;
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
          if (tasks.page) {
            this.page = tasks.page;
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.taskChangeSubscription.unsubscribe();
    this.taskDeletedSubscription.unsubscribe();
    this.workbasketChangeSubscription.unsubscribe();
    this.taskAddedSubscription.unsubscribe();
    this.orientationSubscription.unsubscribe();
  }
}
