import { Component, OnDestroy, OnInit } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { TaskService } from 'app/workplace/services/task.service';
import { Observable, Subject } from 'rxjs';
import { Direction, Sorting, TaskQuerySortParameter } from 'app/shared/models/sorting';
import { Workbasket } from 'app/shared/models/workbasket';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { Page } from 'app/shared/models/page';
import { take, takeUntil } from 'rxjs/operators';
import { Search } from '../task-list-toolbar/task-list-toolbar.component';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { QueryPagingParameter } from '../../../shared/models/query-paging-parameter';
import { TaskQueryFilterParameter } from '../../../shared/models/task-query-filter-parameter';
import { Select, Store } from '@ngxs/store';
import { FilterSelectors } from '../../../shared/store/filter-store/filter.selectors';
import { WorkplaceSelectors } from '../../../shared/store/workplace-store/workplace.selectors';
import { CalculateNumberOfCards } from '../../../shared/store/workplace-store/workplace.actions';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';

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
  taskDefaultSortBy: TaskQuerySortParameter = TaskQuerySortParameter.PRIORITY;
  sort: Sorting<TaskQuerySortParameter> = {
    'sort-by': this.taskDefaultSortBy,
    order: Direction.ASC
  };
  paging: QueryPagingParameter = {
    page: 1,
    'page-size': 9
  };
  filterBy: TaskQueryFilterParameter = {};

  requestInProgress = false;
  selectedSearchType: Search = Search.byWorkbasket;

  destroy$ = new Subject();

  @Select(FilterSelectors.getTaskFilter) filter$: Observable<TaskQueryFilterParameter>;
  @Select(WorkplaceSelectors.getNumberOfCards) cards$: Observable<number>;

  constructor(
    private taskService: TaskService,
    private workplaceService: WorkplaceService,
    private notificationsService: NotificationService,
    private orientationService: OrientationService,
    private store: Store,
    private requestInProgressService: RequestInProgressService
  ) {}

  ngOnInit() {
    this.cards$.pipe(takeUntil(this.destroy$)).subscribe((cards) => {
      this.paging['page-size'] = cards;
      this.getTasks();
    });

    this.taskService.taskSelectedStream.pipe(takeUntil(this.destroy$)).subscribe((task: Task) => {
      this.selectedId = task ? task.taskId : '';
      if (!this.tasks) {
        this.currentBasket = task.workbasketSummary;
        this.getTasks();
      }
    });

    this.taskService.taskChangedStream.pipe(takeUntil(this.destroy$)).subscribe((task) => {
      this.currentBasket = task.workbasketSummary;
      this.getTasks();
    });

    this.taskService.taskDeletedStream.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.selectedId = '';
      this.getTasks();
    });

    this.orientationService
      .getOrientation()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new CalculateNumberOfCards());
      });

    this.workplaceService
      .getSelectedWorkbasket()
      .pipe(takeUntil(this.destroy$))
      .subscribe((workbasket) => {
        this.currentBasket = workbasket;
        if (this.selectedSearchType === Search.byWorkbasket) {
          this.getTasks();
        }
      });
  }

  performSorting(sort: Sorting<TaskQuerySortParameter>) {
    this.sort = sort;
    this.getTasks();
  }

  performFilter() {
    this.paging.page = 1;
    this.filter$.pipe(take(1)).subscribe((filter) => {
      this.filterBy = { ...filter };
      this.getTasks();
    });
  }

  selectSearchType(type: Search) {
    this.selectedSearchType = type;
    this.tasks = [];
  }

  changePage(page) {
    this.paging.page = page;
    this.getTasks();
  }

  ngOnDestroy(): void {
    this.destroy$.next(null);
    this.destroy$.complete();
  }

  private getTasks(): void {
    this.requestInProgress = true;
    this.requestInProgressService.setRequestInProgress(true);

    if (this.selectedSearchType === Search.byTypeAndValue) {
      delete this.currentBasket;
    }

    this.filterBy['workbasket-id'] = [this.currentBasket?.workbasketId];

    if (this.selectedSearchType === Search.byWorkbasket && !this.currentBasket) {
      this.requestInProgress = false;
      this.requestInProgressService.setRequestInProgress(false);
      this.tasks = [];
    } else {
      this.taskService
        .findTasksWithWorkbasket(this.filterBy, this.sort, this.paging)
        .pipe(take(1))
        .subscribe((taskResource) => {
          this.requestInProgress = false;
          this.requestInProgressService.setRequestInProgress(false);
          if (taskResource.tasks && taskResource.tasks.length > 0) {
            this.tasks = taskResource.tasks;
          } else {
            this.tasks = [];
            if (this.selectedSearchType === Search.byWorkbasket) {
              this.notificationsService.showInformation('EMPTY_WORKBASKET');
            }
          }
          this.tasksPageInformation = taskResource.page;
        });
    }
  }
}
