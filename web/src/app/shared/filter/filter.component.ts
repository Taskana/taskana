import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { ICONTYPES } from 'app/models/type';
import { FilterModel } from 'app/models/filter';
import { TaskanaType } from 'app/models/taskana-type';

@Component({
  selector: 'taskana-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent implements OnInit {

  @Input() allTypes: Map<string, string> = new Map([['ALL', 'All'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'],
  ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']]);

  @Input() filterParams = { name: '', key: '', type: '', description: '', owner: '' };

  @Input() filterType = TaskanaType.WORKBASKETS;

  @Output() performFilter = new EventEmitter<FilterModel>();

  filter: FilterModel;
  filterParamKeys = [];
  lastFilterKey: string;
  toggleDropDown = false;

  constructor() {
  }

  ngOnInit(): void {
    this.initializeFilterModel();
    if (this.filterParams) {
      this.filterParamKeys = Object.keys(this.filterParams);
      this.lastFilterKey = this.filterParamKeys[this.filterParamKeys.length - 1];
    }
  }

  selectType(type: ICONTYPES) {
    this.filter.filterParams.type = type;
  }

  clear() {
    for (const key of Object.keys(this.filterParams)) {
      this.filterParams[key] = '';
    }
    this.initializeFilterModel();
  }

  search() {
    this.performFilter.emit(this.filter);
  }

  initializeFilterModel(): void {
    this.filter = new FilterModel(this.filterParams);
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
    const unusedKeys = [];
    for (const key of this.filterParamKeys) {
      if (['name', 'key', 'type'].indexOf(key) < 0) {
        unusedKeys.push(key);
      }
    }
    return unusedKeys;
  }
}
