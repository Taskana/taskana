import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';

export class FilterModel {
  type:string;
  name:string;
  description:string;
  owner:string;
  key: string;
  constructor(type:string = '', name:string = '', description:string = '', owner:string = '', key:string = ''){
    this.type = type;
    this.name = name;
    this.description= description;
    this.owner = owner;
    this.key = key;
  }
}

@Component({
  selector: 'taskana-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent{

  constructor() { }

  filter: FilterModel = new FilterModel();

  @Input()
  target:string;

  @Output()
  performFilter = new EventEmitter<FilterModel>();
  
  selectType(type: number){
    this.filter.type = type === 0 ? 'PERSONAL': type === 1? 'GROUP': '';
  }

  clear(){
    this.filter = new FilterModel();
  }

  search(){
    this.performFilter.emit(this.filter);
  }

}
