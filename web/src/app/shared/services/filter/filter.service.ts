import { Injectable } from '@angular/core';
import { FilterModel } from 'app/models/filter';

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  filter: FilterModel;

  constructor() { }

  setFilter(filter: FilterModel) {
    this.filter = filter;
  }

  getFilter() {
    return this.filter;
  }
}
