import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { IconTypeComponent } from '../type-icon/icon-type.component'
import { ICONTYPES } from '../../model/type';

export class FilterModel {
	type: string;
	name: string;
	description: string;
	owner: string;
	key: string;
	constructor(type: string = '', name: string = '', description: string = '', owner: string = '', key: string = '') {
		this.type = type;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.key = key;
	}
}

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
		this.allTypes = IconTypeComponent.allTypes;
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
