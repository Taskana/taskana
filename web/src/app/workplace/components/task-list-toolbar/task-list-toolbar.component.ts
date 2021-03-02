import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/shared/models/workbasket';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Sorting, TASK_SORT_PARAMETER_NAMING, TaskQuerySortParameter } from 'app/shared/models/sorting';
import { expandDown } from 'app/shared/animations/expand.animation';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { ObjectReference } from 'app/workplace/models/object-reference';
import { TaskQueryFilterParameter } from '../../../shared/models/task-query-filter-parameter';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TaskanaEngineService } from '../../../shared/services/taskana-engine/taskana-engine.service';

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
  toolbarState = false;
  searched = false;

  search = Search;
  searchSelected: Search = Search.byWorkbasket;
  resultType = '';
  resultValue = '';

  destroy$ = new Subject<void>();

  constructor(
    private taskanaEngineService: TaskanaEngineService,
    private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private workplaceService: WorkplaceService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
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
      .subscribe((t) => {
        if (!this.resultName) {
          this.resultName = t.workbasketSummary.name;
          this.resultId = t.workbasketSummary.workbasketId;
          this.currentBasket = t.workbasketSummary;
          this.workplaceService.selectWorkbasket(this.currentBasket);
          this.workbasketSelected = true;
        }
      });

    if (this.route.snapshot.queryParams.search === this.search.byTypeAndValue) {
      this.searchSelected = this.search.byTypeAndValue;
    }
    if (this.router.url.includes('taskdetail')) {
      this.searched = true;
    }
  }

  filterWorkbasketNames() {
    this.filteredWorkbasketNames = this.workbasketNames.filter((value) =>
      value.toLowerCase().includes(this.resultName.toLowerCase())
    );
  }

  searchBasket() {
    this.toolbarState = false;
    this.workbasketSelected = true;
    if (this.searchSelected === this.search.byTypeAndValue) {
      const objectReference = new ObjectReference();
      objectReference.type = this.resultType;
      objectReference.value = this.resultValue;
      this.workplaceService.selectObjectReference(objectReference);
      this.searched = true;
    } else {
      if (this.workbaskets) {
        this.workbaskets.forEach((workbasket) => {
          if (workbasket.name === this.resultName) {
            this.resultId = workbasket.workbasketId;
            this.currentBasket = workbasket;
            this.searched = true;
            this.workplaceService.selectWorkbasket(this.currentBasket);
          }
        });

        if (!this.resultId) {
          delete this.currentBasket;
          this.workplaceService.selectWorkbasket();
        }
      }
    }

    this.resultId = '';
    this.router.navigate(['']);
  }

  sorting(sort: Sorting<TaskQuerySortParameter>) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: TaskQueryFilterParameter) {
    this.performFilter.emit(filterBy);
  }

  createTask() {
    this.taskService.selectTask();
    this.router.navigate([{ outlets: { detail: 'taskdetail/new-task' } }], { relativeTo: this.route });
  }

  selectSearch(type: Search) {
    this.searched = false;
    delete this.resultId;
    delete this.currentBasket;
    this.selectSearchType.emit(type);
    this.searchSelected = type;

    const navigationExtras: NavigationExtras = {
      queryParams: { search: type }
    };

    this.router.navigate([''], navigationExtras);
    this.searchBasket();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
