import { Component, OnInit, ViewChild } from '@angular/core';
import { Direction, Sorting, TaskHistoryQuerySortParameter } from 'app/shared/models/sorting';
import { TaskHistoryEventData } from '../../shared/models/task-history-event';
import { TaskHistoryQueryService } from '../services/task-history-query/task-history-query.service';
import { Page } from '../../shared/models/page';
import { MatSort, Sort } from '@angular/material/sort';
import { QueryPagingParameter } from '../../shared/models/query-paging-parameter';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { merge } from 'rxjs';
import { startWith, switchMap, tap } from 'rxjs/operators';
import { RequestInProgressService } from '../../shared/services/request-in-progress/request-in-progress.service';
import { Pair } from '../../shared/models/pair';

@Component({
  selector: 'taskana-task-query',
  templateUrl: './task-history-query.component.html',
  styleUrls: ['./task-history-query.component.scss']
})
export class TaskHistoryQueryComponent implements OnInit {
  data: TaskHistoryEventData[] = [];
  displayedColumns: Pair<string, TaskHistoryQuerySortParameter>[] = [
    { left: 'parentBusinessProcessId', right: TaskHistoryQuerySortParameter.PARENT_BUSINESS_PROCESS_ID },
    { left: 'businessProcessId', right: TaskHistoryQuerySortParameter.BUSINESS_PROCESS_ID },
    { left: 'created', right: TaskHistoryQuerySortParameter.CREATED },
    { left: 'userId', right: TaskHistoryQuerySortParameter.USER_ID },
    { left: 'eventType', right: TaskHistoryQuerySortParameter.EVENT_TYPE },
    { left: 'workbasketKey', right: TaskHistoryQuerySortParameter.WORKBASKET_KEY },
    { left: 'porType', right: TaskHistoryQuerySortParameter.POR_TYPE },
    { left: 'porValue', right: TaskHistoryQuerySortParameter.POR_VALUE },
    { left: 'domain', right: TaskHistoryQuerySortParameter.DOMAIN },
    { left: 'taskId', right: TaskHistoryQuerySortParameter.TASK_ID },
    { left: 'porCompany', right: TaskHistoryQuerySortParameter.POR_COMPANY },
    { left: 'porSystem', right: TaskHistoryQuerySortParameter.POR_SYSTEM },
    { left: 'porInstance', right: TaskHistoryQuerySortParameter.POR_INSTANCE },
    { left: 'taskClassificationKey', right: TaskHistoryQuerySortParameter.TASK_CLASSIFICATION_KEY },
    { left: 'taskClassificationCategory', right: TaskHistoryQuerySortParameter.TASK_CLASSIFICATION_CATEGORY },
    { left: 'attachmentClassificationKey', right: TaskHistoryQuerySortParameter.ATTACHMENT_CLASSIFICATION_KEY },
    { left: 'custom1', right: TaskHistoryQuerySortParameter.CUSTOM_1 },
    { left: 'custom2', right: TaskHistoryQuerySortParameter.CUSTOM_2 },
    { left: 'custom3', right: TaskHistoryQuerySortParameter.CUSTOM_3 },
    { left: 'custom4', right: TaskHistoryQuerySortParameter.CUSTOM_4 },
    { left: 'comment', right: undefined },
    { left: 'oldValue', right: undefined },
    { left: 'newValue', right: undefined },
    { left: 'oldData', right: undefined },
    { left: 'newData', right: undefined }
  ];
  pageInformation: Page;

  pageParameter: QueryPagingParameter = {
    page: 1,
    'page-size': 9
  };

  // IMPORTANT: Please make sure that material table default matches with this entity.
  sortParameter: Sorting<TaskHistoryQuerySortParameter> = {
    'sort-by': TaskHistoryQuerySortParameter.CREATED,
    order: Direction.ASC
  };

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(PaginationComponent) pagination: PaginationComponent;

  constructor(
    private taskHistoryQueryService: TaskHistoryQueryService,
    private requestInProgressService: RequestInProgressService
  ) {}

  ngOnInit() {}

  ngAfterViewInit() {
    const sortChange$ = this.sort.sortChange.pipe(
      tap((sort) => this.updateSortParameter(sort)),
      tap(() => (this.pageParameter.page = 1))
    );

    const pageChange$ = this.pagination.changePage.pipe(tap((newPage) => (this.pageParameter.page = newPage)));

    merge(sortChange$, pageChange$)
      .pipe(
        startWith({}),
        tap(() => this.calculateQueryPages()),
        switchMap(() => {
          this.requestInProgressService.setRequestInProgress(true);
          return this.taskHistoryQueryService.getTaskHistoryEvents(undefined, this.sortParameter, this.pageParameter);
        })
      )
      .subscribe((data) => {
        this.data = data.taskHistoryEvents;
        this.pageInformation = data.page;
        this.requestInProgressService.setRequestInProgress(false);
      });
  }

  updateSortParameter(sort: Sort): void {
    if (sort) {
      const pair: Pair<string, TaskHistoryQuerySortParameter> = this.displayedColumns.find(
        (pair) => pair.left === sort.active
      );
      if (pair) {
        this.sortParameter = {
          'sort-by': pair.right,
          order: sort.direction === 'asc' ? Direction.ASC : Direction.DESC
        };
      }
    }
  }

  // this is a workaround so that the variables inside the html will be resolved correctly.
  // more details: https://stackoverflow.com/a/64448113/6501286
  convertToTaskHistoryEventData(data: TaskHistoryEventData): TaskHistoryEventData {
    return data;
  }

  getDisplayColumns(): string[] {
    return this.displayedColumns.map((pair) => pair.left);
  }

  private calculateQueryPages() {
    const rowHeight = 48;
    const unusedHeight = 300;
    const totalHeight = window.innerHeight;
    const cards = Math.round((totalHeight - unusedHeight) / rowHeight);
    this.pageParameter['page-size'] = cards > 0 ? cards : 1;
  }
}
