import { Component, OnInit, Output, EventEmitter } from '@angular/core';

export enum Direction {
	ASC = 'asc',
	DESC = 'desc'
};


  export class SortingModel {
    sortBy:string;
    sortDirection:string;
    constructor(sortBy:string = 'key', sortDirection:Direction = Direction.ASC){
      this.sortBy = sortBy;
      this.sortDirection = sortDirection;
    }
  }

@Component({
  selector: 'taskana-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.scss']
})
export class SortComponent implements OnInit {
	readonly sortingFields: Map<string, string> = new Map([['name', 'Name'], ['key', 'Id'], ['description', 'Description'], ['owner', 'Owner'], ['type', 'Type']]);

  constructor() { }

  @Output()
  performSorting = new EventEmitter<SortingModel>();
  sort: SortingModel = new SortingModel();

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
  
  private search(){
    this.performSorting.emit(this.sort);
  }

}
