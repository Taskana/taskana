import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { ICONTYPES } from 'app/shared/models/icon-types';
import { Filter } from 'app/shared/models/filter';
import { TaskanaType } from 'app/shared/models/taskana-type';

@Component({
  selector: 'taskana-shared-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent implements OnInit {
  @Input() allTypes: Map<string, string> = new Map([
    ['ALL', 'All'],
    ['PERSONAL', 'Personal'],
    ['GROUP', 'Group'],
    ['CLEARANCE', 'Clearance'],
    ['TOPIC', 'Topic']
  ]);

  @Input() allStates: Map<string, string> = new Map([
    ['ALL', 'All'],
    ['READY', 'Ready'],
    ['CLAIMED', 'Claimed'],
    ['COMPLETED', 'Completed']
  ]);

  @Input() filterParams = { name: '', key: '', type: '', description: '', owner: '' };

  @Input() filterType = TaskanaType.WORKBASKETS;

  @Output() performFilter = new EventEmitter<Filter>();

  filter: Filter;
  filterParamKeys = [];
  lastFilterKey: string;
  toggleDropDown = false;

  ngOnInit(): void {
    this.initializeFilterModel();
    if (this.filterParams) {
      this.filterParamKeys = Object.keys(this.filterParams);
      this.lastFilterKey = this.filterParamKeys[this.filterParamKeys.length - 1];
    }
  }

  selectType(type: ICONTYPES) {
    this.filter.filterParams.type = type === ICONTYPES.ALL ? '' : type;
  }

  selectState(state: ICONTYPES) {
    this.filter.filterParams.state = state === 'ALL' ? '' : state;
  }

  clear() {
    Object.keys(this.filterParams).forEach((key) => {
      this.filterParams[key] = '';
    });
    this.initializeFilterModel();
  }

  search() {
    this.performFilter.emit(this.filter);
  }

  initializeFilterModel(): void {
    this.filter = new Filter(this.filterParams);
  }

  checkUppercaseFilterType(filterType: string) {
    return filterType === 'type' || filterType === 'state';
  }

  filterTypeIsWorkbasket(): boolean {
    return this.filterType === TaskanaType.WORKBASKETS;
  }

  /**
   * keys that are hardcoded in the HTML need to be specified here
   * @returns {string[]}
   */
  getUnusedKeys(): string[] {
    return Object.keys(this.filterParamKeys).filter((key) => ['name', 'key', 'type'].indexOf(key) < 0);
  }
}
