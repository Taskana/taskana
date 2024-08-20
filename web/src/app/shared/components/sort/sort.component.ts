import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Direction, Sorting } from 'app/shared/models/sorting';

@Component({
  selector: 'kadai-shared-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.scss']
})
export class SortComponent<T> implements OnInit {
  @Input() sortingFields: Map<T, string>;
  @Input() menuPosition = 'right';
  @Input() defaultSortBy: T;

  @Output() performSorting = new EventEmitter<Sorting<T>>();

  sort: Sorting<T> = {
    'sort-by': undefined,
    order: Direction.ASC
  };

  // this allows the html template to use the Direction enum.
  sortDirectionEnum = Direction;

  ngOnInit() {
    this.sort['sort-by'] = this.defaultSortBy;
  }

  changeOrder(sortDirection: Direction) {
    this.sort.order = sortDirection;
    this.search();
  }

  changeSortBy(sortBy: T) {
    this.sort['sort-by'] = sortBy;
    this.search();
  }

  private search() {
    this.performSorting.emit(this.sort);
  }
}
