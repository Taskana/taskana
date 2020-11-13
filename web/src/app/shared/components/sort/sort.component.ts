import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Direction, Sorting } from 'app/shared/models/sorting';

@Component({
  selector: 'taskana-shared-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.scss']
})
export class SortComponent implements OnInit {
  @Input() sortingFields: Map<string, string>;
  @Input() menuPosition = 'right';
  @Input() defaultSortBy = 'access-id';

  @Output() performSorting = new EventEmitter<Sorting>();

  sort: Sorting = new Sorting();

  ngOnInit() {
    this.sort.sortBy = this.defaultSortBy;
  }

  changeOrder(sortDirection: string) {
    this.sort.sortDirection = sortDirection === Direction.ASC ? Direction.ASC : Direction.DESC;
    this.search();
  }

  changeSortBy(sortBy: string) {
    this.sort.sortBy = sortBy;
    console.log(sortBy);
    this.search();
  }

  private search() {
    this.performSorting.emit(this.sort);
  }
}
