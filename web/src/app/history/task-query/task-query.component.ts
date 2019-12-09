import { Component, OnInit } from '@angular/core';
import { TaskQueryService } from '../services/task-query/task-query.service';
import { TaskHistoryEventData } from '../../models/task-history-event';
import { SortingModel, Direction } from 'app/models/sorting';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { Subscription } from 'rxjs';
import { Orientation } from 'app/models/orientation';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { MessageModal } from 'app/models/message-modal';
import { FormGroup, FormControl } from '@angular/forms';
import { TaskHistoryEventResourceData } from 'app/models/task-history-event-resource';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

@Component({
  selector: 'taskana-task-query',
  templateUrl: './task-query.component.html',
  styleUrls: ['./task-query.component.scss']
})
export class TaskQueryComponent implements OnInit {

  taskQueryResource: TaskHistoryEventResourceData;
  taskQuery: Array<TaskHistoryEventData>
  taskQueryHeader = new TaskHistoryEventData();
  orderBy = new SortingModel(TaskanaQueryParameters.parameters.CREATED);
  orientationSubscription: Subscription;
  taskQuerySubscription: Subscription;

  taskQueryForm = new FormGroup({
  });

  constructor(
    private taskQueryService: TaskQueryService,
    private orientationService: OrientationService,
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService, ) { }

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
      return (fieldName === 'created')
  }

  filterFieldsToAllowQuerying(fieldName: string): boolean {
    if (!fieldName || fieldName === 'oldData' || fieldName === 'newData' || fieldName === 'comment'
      || fieldName === 'oldValue' || fieldName === 'newValue') {
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
    if (fieldName === 'custom1' || fieldName === 'custom2' || fieldName === 'custom3' || fieldName === 'custom4'
      || fieldName === 'oldData' || fieldName === 'newData' || fieldName === 'comment'
      || fieldName === 'oldValue' || fieldName === 'newValue') {
      return true;
    }
    return false;
  }

  search() {
    this.performRequest();
  }

  changeOrderBy(key: string) {
    if (!this.filterFieldsToAllowQuerying(key)) {
      return null;
    }
    if (this.orderBy.sortBy === key) {
      this.orderBy.sortDirection = this.toggleSortDirection(this.orderBy.sortDirection);
    }
    this.orderBy.sortBy = key;
  }

  openDetails(key: string, val: string) {
    this.generalModalService.triggerMessage(
      new MessageModal(
        `These are the details of ${this.getHeaderFieldDescription(key)}`,
        val,
        'code'
      )
    )
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

  private toggleSortDirection(sortDirection: string): Direction {
    if (sortDirection === Direction.ASC) {
      return Direction.DESC;
    }
    return Direction.ASC
  }

  private performRequest() {
    this.requestInProgressService.setRequestInProgress(true);
    this.calculateQueryPages();
    this.taskQuerySubscription = this.taskQueryService.queryTask(
      this.orderBy.sortBy.replace(/([A-Z])|([0-9])/g, (g) => `-${g[0].toLowerCase()}`),
      this.orderBy.sortDirection,
      new TaskHistoryEventData(this.taskQueryForm.value),
      false).subscribe(taskQueryResource => {
        this.requestInProgressService.setRequestInProgress(false);
        if (!taskQueryResource.taskHistoryEvents) {
          this.taskQuery = null;
          this.taskQueryResource = null;
          return null;
        }
        this.taskQueryResource = taskQueryResource;
        this.taskQuery = taskQueryResource.taskHistoryEvents;
      })
  }

  private initTaskQueryForm() {
    const me = this;
    Object.keys(new TaskHistoryEventData()).forEach(function (key) {
      me.taskQueryForm.addControl(key, new FormControl());
    });
  }

  private calculateQueryPages() {

    const rowHeight = 34;
    const unusedHeight = 300;
    const totalHeight = window.innerHeight;
    const cards = Math.round((totalHeight - (unusedHeight)) / rowHeight);
    TaskanaQueryParameters.page ? TaskanaQueryParameters.page = TaskanaQueryParameters.page : TaskanaQueryParameters.page = 1;
    cards > 0 ? TaskanaQueryParameters.pageSize = cards : TaskanaQueryParameters.pageSize = 1;
  }

  updateDate($event: string) {
      this.taskQueryForm.get('created').setValue($event.substring(0, 10));
      this.performRequest();
  }

  onDestroy() {
    if (this.orientationSubscription) { this.orientationSubscription.unsubscribe(); }
    if (this.taskQuerySubscription) { this.taskQuerySubscription.unsubscribe(); }
  }

}
