import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ALL_TYPES, WorkbasketType } from '../../models/workbasket-type';
import { WorkbasketQueryFilterParameter } from '../../models/workbasket-query-filter-parameter';
import { Pair } from '../../models/pair';

@Component({
  selector: 'taskana-shared-workbasket-filter',
  templateUrl: './workbasket-filter.component.html',
  styleUrls: ['./workbasket-filter.component.scss']
})
export class WorkbasketFilterComponent implements OnInit {
  allTypes: Map<WorkbasketType, string> = ALL_TYPES;

  @Input() component: string;
  @Input() isExpanded: boolean;

  @Output() performFilter = new EventEmitter<Pair<string, WorkbasketQueryFilterParameter>>();

  filter: WorkbasketQueryFilterParameter;

  ngOnInit(): void {
    this.clear();
  }

  clear() {
    this.filter = {
      'name-like': [],
      'key-like': [],
      'description-like': [],
      'owner-like': [],
      type: []
    };
  }

  selectType(type: WorkbasketType) {
    this.filter.type = type ? [type] : [];
  }

  search() {
    this.performFilter.emit({ left: this.component, right: this.filter });
  }
}
