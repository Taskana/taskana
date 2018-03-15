import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../../../../model/workbasket-summary';
import { FilterModel } from '../../../../shared/filter/filter.component';
import { filter } from 'rxjs/operators';
import { Side } from '../distribution-targets.component';

@Component({
	selector: 'taskana-dual-list',
	templateUrl: './dual-list.component.html',
	styleUrls: ['./dual-list.component.scss']
})
export class DualListComponent implements OnInit {

	constructor() { }

	ngOnInit() {
		this.sideNumber = this.side === Side.LEFT ? 0 : 1;
	}

	@Input() distributionTargets: Array<WorkbasketSummary>;
	@Output() distributionTargetsChange = new EventEmitter<Array<WorkbasketSummary>>();
	@Input() distributionTargetsSelected: Array<WorkbasketSummary>;
	@Output() performDualListFilter = new EventEmitter<{ filterBy: FilterModel, side: Side }>();
	@Input() requestInProgress: boolean = false;

	@Input() side: Side;
	sideNumber: number = 0;

	selectAll(selected: boolean) {
		this.distributionTargets.forEach((element: any) => {
			element.selected = selected;
		});
	}


	performAvailableFilter(filterModel: FilterModel) {
		this.performDualListFilter.emit({ filterBy: filterModel, side: this.side });
	}

}
