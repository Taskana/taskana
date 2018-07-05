import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Direction, SortingModel} from 'app/models/sorting';

@Component({
  selector: 'taskana-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.scss']
})
export class SortComponent implements OnInit {
  @Input() sortingFields: Map<string, string>;
  @Input() enabled = true;

  @Output() performSorting = new EventEmitter<SortingModel>();

  sort: SortingModel = new SortingModel();

  constructor() {
  }

  ngOnInit() {
  }

  changeOrder(sortDirection: string) {
    this.sort.sortDirection = (sortDirection === Direction.ASC) ? Direction.ASC : Direction.DESC;
    this.search();
  }

  changeSortBy(sortBy: string) {
    this.sort.sortBy = sortBy;
    this.search();
  }

  private search() {
    this.performSorting.emit(this.sort);
  }

}
