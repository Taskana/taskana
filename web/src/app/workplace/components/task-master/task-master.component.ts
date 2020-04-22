import { Component, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { TaskService } from 'app/workplace/services/task.service';
import { Subscription } from 'rxjs';
import { Sorting } from 'app/shared/models/sorting';
import { Workbasket } from 'app/shared/models/workbasket';
import { Filter } from 'app/shared/models/filter';
import { AlertService } from 'app/shared/services/alert/alert.service';
import { AlertModel, AlertType } from 'app/shared/models/alert';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { Orientation } from 'app/shared/models/orientation';
import { Page } from 'app/shared/models/page';
import { ObjectReference } from '../../models/object-reference';
import { Search } from '../task-list-toolbar/task-list-toolbar.component';

@Component({
  selector: 'taskana-task-master',
  templateUrl: './task-master.component.html',
  styleUrls: ['./task-master.component.scss']
})
export class TaskMasterComponent implements OnInit, OnDestroy {
  tasks: Task[];
  tasksPageInformation: Page;
  type = 'tasks';
  currentBasket: Workbasket;
  selectedId = '';
  taskDefaultSortBy: string = 'priority';
  sort: Sorting = new Sorting(this.taskDefaultSortBy);
  filterBy: Filter = new Filter({
    name: '',
    owner: '',
    priority: '',
    state: '',
    classificationKey: '',
    workbasketId: '',
    workbasketKey: ''
  });

  requestInProgress = false;
  objectReference: ObjectReference;
  selectedSearchType: Search = Search.byWorkbasket;

  @ViewChild('wbToolbar', { static: true })
  private toolbarElement: ElementRef;

  private taskChangeSubscription: Subscription;
  private taskDeletedSubscription: Subscription;
  private taskAddedSubscription: Subscription;
  private workbasketChangeSubscription: Subscription;
  private orientationSubscription: Subscription;
  private objectReferenceSubscription: Subscription;
  constructor(
    private taskService: TaskService,
    private workplaceService: WorkplaceService,
    private alertService: AlertService,
    private orientationService: OrientationService
  ) {
    this.taskChangeSubscription = this.taskService.taskChangedStream.subscribe(task => {
      this.getTasks();
      this.selectedId = task ? task.taskId : '';
    });

    this.workbasketChangeSubscription = this.workplaceService.workbasketSelectedStream.subscribe(workbasket => {
      this.currentBasket = workbasket;
      if (this.selectedSearchType === Search.byWorkbasket) {
        this.getTasks();
      }
    });

    this.objectReferenceSubscription = this.workplaceService.objectReferenceSelectedStream.subscribe(objectReference => {
      this.objectReference = objectReference;
      delete this.currentBasket;
      if (objectReference) {
        this.getTasks();
      }
    });
  }

  ngOnInit() {
    this.taskService.getSelectedTask().subscribe(
      task => {
        if (!this.currentBasket) {
          this.selectedId = task.taskId;
          this.currentBasket = task.workbasketSummaryResource;
        }
        if (!task) {
          this.selectedId = '';
        }
      }
    );
    this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
      this.refreshWorkbasketList();
    });
  }


  performSorting(sort: Sorting) {
    this.sort = sort;
    this.getTasks();
  }

  performFilter(filterBy: Filter) {
    this.filterBy = filterBy;
    this.getTasks();
  }

  selectSearchType(type: Search) {
    this.selectedSearchType = type;
    this.tasks = [];
  }

  changePage(page) {
    TaskanaQueryParameters.page = page;
    this.getTasks();
  }

  private refreshWorkbasketList() {
    this.calculateHeightCard();
    this.getTasks();
  }

  private calculateHeightCard() {
    if (this.toolbarElement) {
      const toolbarSize = this.toolbarElement.nativeElement.offsetHeight;
      const cardHeight = 53;
      const unusedHeight = 150;
      const totalHeight = window.innerHeight;
      const cards = Math.round((totalHeight - (unusedHeight + toolbarSize)) / cardHeight);
      TaskanaQueryParameters.page = TaskanaQueryParameters.page ? TaskanaQueryParameters.page : 1;
      TaskanaQueryParameters.pageSize = cards > 0 ? cards : 1;
    }
  }

  private getTasks(): void {
    this.requestInProgress = true;
    if (!this.currentBasket && !this.objectReference) {
      this.requestInProgress = false;
      this.tasks = [];
    } else {
      this.calculateHeightCard();
      this.taskService.findTasksWithWorkbasket(this.currentBasket ? this.currentBasket.workbasketId : '',
        this.sort.sortBy, this.sort.sortDirection, this.filterBy.filterParams.name, this.filterBy.filterParams.owner,
        this.filterBy.filterParams.priority, this.filterBy.filterParams.state, this.objectReference ? this.objectReference.type : '',
        this.objectReference ? this.objectReference.value : '')
        .subscribe(tasks => {
          this.requestInProgress = false;
          if (tasks.tasks) {
            this.tasks = tasks.tasks;
          } else {
            this.tasks = [];
            this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'The selected Workbasket is empty!'));
          }
          this.tasksPageInformation = tasks.page;
        });
    }
  }

  ngOnDestroy(): void {
    if (this.taskChangeSubscription) { this.taskChangeSubscription.unsubscribe(); }
    if (this.taskDeletedSubscription) { this.taskDeletedSubscription.unsubscribe(); }
    if (this.workbasketChangeSubscription) { this.workbasketChangeSubscription.unsubscribe(); }
    if (this.taskAddedSubscription) { this.taskAddedSubscription.unsubscribe(); }
    if (this.orientationSubscription) { this.orientationSubscription.unsubscribe(); }
    if (this.objectReferenceSubscription) { this.objectReferenceSubscription.unsubscribe(); }
  }
}
