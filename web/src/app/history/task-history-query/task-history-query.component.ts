import { Component, OnInit } from '@angular/core';
import { Direction, Sorting, TaskHistoryQuerySortParameter } from 'app/shared/models/sorting';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { Subscription } from 'rxjs';
import { Orientation } from 'app/shared/models/orientation';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { FormControl, FormGroup } from '@angular/forms';
import { TaskHistoryEventResourceData } from 'app/shared/models/task-history-event-resource';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';
import { TaskHistoryEventData } from '../../shared/models/task-history-event';
import { TaskHistoryQueryService } from '../services/task-history-query/task-history-query.service';

@Component({
  selector: 'taskana-task-query',
  templateUrl: './task-history-query.component.html',
  styleUrls: ['./task-history-query.component.scss']
})
export class TaskHistoryQueryComponent implements OnInit {
  taskQueryResource: TaskHistoryEventResourceData;
  taskQuery: Array<TaskHistoryEventData>;
  taskQueryHeader = new TaskHistoryEventData();
  orderBy: Sorting<TaskHistoryQuerySortParameter> = {
    'sort-by': TaskHistoryQuerySortParameter.CREATED,
    order: Direction.ASC
  };
  orientationSubscription: Subscription;
  taskQuerySubscription: Subscription;

  taskQueryForm = new FormGroup({});

  constructor(
    private taskQueryService: TaskHistoryQueryService,
    private orientationService: OrientationService,
    private requestInProgressService: RequestInProgressService
  ) {}

  ngOnInit() {
    this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
      this.performRequest();
    });
    this.initTaskQueryForm();
  }

  getHeaderFieldDescription(property: string): string {
    switch (property) {
      case 'parentBusinessProcessId':
        return 'Parent BPI';
      case 'businessProcessId':
        return 'BPI';
      case 'taskId':
        return 'Task id';
      case 'eventType':
        return 'Event type';
      case 'created':
        return 'Created';
      case 'userId':
        return 'User id';
      case 'domain':
        return 'Domain';
      case 'workbasketKey':
        return 'Workbasket key';
      case 'porCompany':
        return 'Obj company';
      case 'porSystem':
        return 'Obj system';
      case 'porInstance':
        return 'Obj instance';
      case 'porType':
        return 'Obj type';
      case 'porValue':
        return 'Obj value';
      case 'taskClassificationKey':
        return 'Classification key';
      case 'taskClassificationCategory':
        return 'Classification category';
      case 'attachmentClassificationKey':
        return 'Attachment Classification';
      case 'custom1':
        return 'Custom 1';
      case 'custom2':
        return 'Custom 2';
      case 'custom3':
        return 'Custom 3';
      case 'custom4':
        return 'Custom 4';
      case 'oldData':
        return 'Old data';
      case 'newData':
        return 'New data';
      case 'comment':
        return 'Comment';
      case 'oldValue':
        return 'Old value';
      case 'newValue':
        return 'New value';
      default:
        return property;
    }
  }

  isDate(fieldName: string): boolean {
    return fieldName === 'created';
  }

  filterFieldsToAllowQuerying(fieldName: string): boolean {
    if (
      !fieldName ||
      fieldName === 'oldData' ||
      fieldName === 'newData' ||
      fieldName === 'comment' ||
      fieldName === 'oldValue' ||
      fieldName === 'newValue'
    ) {
      return false;
    }

    return true;
  }

  filterFieldsToShow(fieldName: string): boolean {
    if (fieldName === 'taskHistoryId' || fieldName === 'page' || fieldName === '_links') {
      return false;
    }
    return true;
  }

  filterExpandGroup(fieldName: string): boolean {
    if (
      fieldName === 'custom1' ||
      fieldName === 'custom2' ||
      fieldName === 'custom3' ||
      fieldName === 'custom4' ||
      fieldName === 'oldData' ||
      fieldName === 'newData' ||
      fieldName === 'comment' ||
      fieldName === 'oldValue' ||
      fieldName === 'newValue'
    ) {
      return true;
    }
    return false;
  }

  // TODO: but why?
  search() {
    this.performRequest();
  }

  changeOrderBy(key: string) {
    console.log(key);
    if (this.filterFieldsToAllowQuerying(key)) {
      // if (this.orderBy.sortBy === key) {
      //   this.orderBy.sortDirection = this.toggleSortDirection(this.orderBy.sortDirection);
      // }
      // this.orderBy.sortBy = key;
    }
  }

  getTaskValue(key: string, task: TaskHistoryEventData): string {
    return task[key];
  }

  clear() {
    this.taskQueryForm.reset();
    this.performRequest();
  }

  changePage(page) {
    TaskanaQueryParameters.page = page;
    this.performRequest();
  }

  updateDate($event: string) {
    this.taskQueryForm.get('created').setValue($event.substring(0, 10));
    this.performRequest();
  }

  ngOnDestroy() {
    if (this.orientationSubscription) {
      this.orientationSubscription.unsubscribe();
    }
    if (this.taskQuerySubscription) {
      this.taskQuerySubscription.unsubscribe();
    }
  }

  private toggleSortDirection(sortDirection: string): Direction {
    if (sortDirection === Direction.ASC) {
      return Direction.DESC;
    }
    return Direction.ASC;
  }

  private performRequest() {
    this.requestInProgressService.setRequestInProgress(true);
    this.calculateQueryPages();
    this.taskQuerySubscription = this.taskQueryService
      .getTaskHistoryEvents
      // this.orderBy.sortBy.replace(/([A-Z])|([0-9])/g, (g) => `-${g[0].toLowerCase()}`),
      // this.orderBy.sortDirection,
      // new TaskHistoryEventData(this.taskQueryForm.value),
      // false
      ()
      .subscribe((taskQueryResource) => {
        this.requestInProgressService.setRequestInProgress(false);
        this.taskQueryResource = taskQueryResource.taskHistoryEvents ? taskQueryResource : null;
        this.taskQuery = taskQueryResource.taskHistoryEvents ? taskQueryResource.taskHistoryEvents : null;
      });
  }

  private initTaskQueryForm() {
    Object.keys(new TaskHistoryEventData()).forEach((key) => {
      this.taskQueryForm.addControl(key, new FormControl());
    });
    this.performRequest();
  }

  private calculateQueryPages() {
    const rowHeight = 34;
    const unusedHeight = 300;
    const totalHeight = window.innerHeight;
    const cards = Math.round((totalHeight - unusedHeight) / rowHeight);
    TaskanaQueryParameters.page = TaskanaQueryParameters.page ? TaskanaQueryParameters.page : 1;
    TaskanaQueryParameters.pageSize = cards > 0 ? cards : 1;
  }
}
