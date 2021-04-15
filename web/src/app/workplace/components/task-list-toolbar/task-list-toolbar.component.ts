import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/shared/models/workbasket';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Sorting, TASK_SORT_PARAMETER_NAMING, TaskQuerySortParameter } from 'app/shared/models/sorting';
import { expandDown } from 'app/shared/animations/expand.animation';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { TaskQueryFilterParameter } from '../../../shared/models/task-query-filter-parameter';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';
import { Actions, ofActionCompleted, Select, Store } from '@ngxs/store';
import { ClearTaskFilter, SetTaskFilter } from '../../../shared/store/filter-store/filter.actions';
import { WorkplaceSelectors } from '../../../shared/store/workplace-store/workplace.selectors';
import { SetFilterExpansion } from '../../../shared/store/workplace-store/workplace.actions';

export enum Search {
  byWorkbasket = 'workbasket',
  byTypeAndValue = 'type-and-value'
}

@Component({
  selector: 'taskana-task-list-toolbar',
  animations: [expandDown],
  templateUrl: './task-list-toolbar.component.html',
  styleUrls: ['./task-list-toolbar.component.scss']
})
export class TaskListToolbarComponent implements OnInit {
  @Input() taskDefaultSortBy: TaskQuerySortParameter;
  @Output() performSorting = new EventEmitter<Sorting<TaskQuerySortParameter>>();
  @Output() performFilter = new EventEmitter<TaskQueryFilterParameter>();
  @Output() selectSearchType = new EventEmitter();

  sortingFields: Map<TaskQuerySortParameter, string> = TASK_SORT_PARAMETER_NAMING;

  tasks: Task[] = [];
  workbasketNames: string[] = [];
  filteredWorkbasketNames: string[] = this.workbasketNames;
  resultName = '';
  resultId = '';
  workbaskets: Workbasket[];
  currentBasket: Workbasket;
  workbasketSelected = false;
  searched = false;

  search = Search;
  searchSelected: Search = Search.byWorkbasket;
  activeTab: number = 0;
  filterInput = '';

  @Select(WorkplaceSelectors.getFilterExpansion) isFilterExpanded$: Observable<boolean>;

  destroy$ = new Subject<void>();

  constructor(
    private taskanaEngineService: TaskanaEngineService,
    private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private workplaceService: WorkplaceService,
    private router: Router,
    private route: ActivatedRoute,
    private store: Store,
    private ngxsActions$: Actions
  ) {}

  ngOnInit() {
    this.ngxsActions$.pipe(ofActionCompleted(ClearTaskFilter), takeUntil(this.destroy$)).subscribe(() => {
      this.filterInput = '';
    });

    this.workbasketService
      .getAllWorkBaskets()
      .pipe(takeUntil(this.destroy$))
      .subscribe((workbaskets) => {
        this.workbaskets = workbaskets.workbaskets;
        this.workbaskets.forEach((workbasket) => {
          this.workbasketNames.push(workbasket.name);
        });

        // get workbasket of current user
        const user = this.taskanaEngineService.currentUserInfo;
        const filteredWorkbasketsByUser = this.workbaskets.filter(
          (workbasket) => workbasket.key == user.userId || workbasket.key == user.userId.toUpperCase()
        );
        if (filteredWorkbasketsByUser.length > 0) {
          const workbasketOfUser = filteredWorkbasketsByUser[0];
          this.resultName = workbasketOfUser.name;
          this.resultId = workbasketOfUser.workbasketId;
          this.workplaceService.selectWorkbasket(workbasketOfUser);
          this.currentBasket = workbasketOfUser;
          this.workbasketSelected = true;
          this.searched = true;
        }
      });

    this.taskService
      .getSelectedTask()
      .pipe(takeUntil(this.destroy$))
      .subscribe((task) => {
        if (typeof task !== 'undefined') {
          const workbasketSummary = task.workbasketSummary;
          if (this.searchSelected === this.search.byWorkbasket && this.resultName !== workbasketSummary.name) {
            this.resultName = workbasketSummary.name;
            this.resultId = workbasketSummary.workbasketId;
            this.currentBasket = workbasketSummary;
            this.workplaceService.selectWorkbasket(this.currentBasket);
            this.workbasketSelected = true;
          }
        }
      });

    this.route.queryParams.subscribe((params) => {
      const component = params.component;
      if (component == 'workbaskets') {
        this.activeTab = 0;
        if (this.currentBasket) {
          this.resultName = this.currentBasket.name;
          this.resultId = this.currentBasket.workbasketId;
        }
        this.selectSearch(this.search.byWorkbasket);
      }
      if (component == 'task-search') {
        this.activeTab = 1;
        this.searched = true;
        this.selectSearch(this.search.byTypeAndValue);
      }
    });

    if (this.router.url.includes('taskdetail')) {
      this.searched = true;
    }
  }

  setFilterExpansion() {
    this.store.dispatch(new SetFilterExpansion());
  }

  onTabChange(search) {
    const tab = search.path[0].innerText;
    if (tab === 'Workbaskets') {
      this.router.navigate(['taskana/workplace'], { queryParams: { component: 'workbaskets' } });
    }
    if (tab === 'Task search') {
      this.router.navigate(['taskana/workplace'], { queryParams: { component: 'task-search' } });
    }
  }

  updateState() {
    const wildcardFilter: TaskQueryFilterParameter = {
      'wildcard-search-value': [this.filterInput]
    };
    this.store.dispatch(new SetTaskFilter(wildcardFilter));
  }

  filterWorkbasketNames() {
    this.filteredWorkbasketNames = this.workbasketNames.filter((value) =>
      value.toLowerCase().includes(this.resultName.toLowerCase())
    );
  }

  searchBasket() {
    this.store.dispatch(new SetFilterExpansion(false));
    this.workbasketSelected = true;
    if (this.searchSelected === this.search.byWorkbasket && this.workbaskets) {
      this.workbaskets.forEach((workbasket) => {
        if (workbasket.name === this.resultName) {
          this.resultId = workbasket.workbasketId;
          this.currentBasket = workbasket;
          this.workplaceService.selectWorkbasket(this.currentBasket);
        }
      });

      this.searched = !!this.currentBasket;

      if (!this.resultId) {
        delete this.currentBasket;
        this.workplaceService.selectWorkbasket();
      }
    }

    this.resultId = '';
  }

  navigateBack() {
    this.router.navigate([''], { queryParamsHandling: 'merge' });
  }

  sorting(sort: Sorting<TaskQuerySortParameter>) {
    this.performSorting.emit(sort);
  }

  onFilter() {
    this.performFilter.emit();
  }

  onClearFilter() {
    this.store.dispatch(new ClearTaskFilter()).subscribe(() => {
      this.performFilter.emit();
    });
  }

  createTask() {
    this.taskService.selectTask();
    this.router.navigate([{ outlets: { detail: 'taskdetail/new-task' } }], {
      relativeTo: this.route,
      queryParamsHandling: 'merge'
    });
  }

  selectSearch(type: Search) {
    this.searchSelected = type;
    delete this.resultId;
    this.selectSearchType.emit(type);
    this.searchBasket();
    this.onClearFilter();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
