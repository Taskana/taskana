import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ICONTYPES } from 'app/models/type';
import { FilterModel } from 'app/models/filter';

@Component({
	selector: 'taskana-filter',
	templateUrl: './filter.component.html',
	styleUrls: ['./filter.component.scss']
})
export class FilterComponent {


	allTypes: Map<string, string>;
	filter: FilterModel = new FilterModel();

	@Input()
	target: string;

	@Output()
	performFilter = new EventEmitter<FilterModel>();

	toggleDropDown = false;

	constructor() {
		this.allTypes = new Map([['', 'All'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'],
		['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']]);
	}

	selectType(type: ICONTYPES) {
		this.filter.type = type;
	}

	clear() {
		this.filter = new FilterModel();
	}

	search() {
		this.performFilter.emit(this.filter);
	}

}
