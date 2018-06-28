import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { trigger, state, style, transition, animate, keyframes } from '@angular/animations';
import { FilterModel } from 'app/models/filter';
import { filter } from 'rxjs/operators';
import { Side } from '../distribution-targets.component';

@Component({
	selector: 'taskana-dual-list',
	templateUrl: './dual-list.component.html',
	styleUrls: ['./dual-list.component.scss'],
	animations: [
		trigger('toggle', [
			state('*', style({ opacity: '1' })),
			state('void', style({ opacity: '0' })),
			transition('void => *', animate('300ms ease-in', keyframes([
				style({ opacity: 0, height: '0px' }),
				style({ opacity: 0.5, height: '50px' }),
				style({ opacity: 1, height: '*' })]))),
			transition('* => void', animate('300ms ease-out', keyframes([
				style({ opacity: 1, height: '*' }),
				style({ opacity: 0.5, height: '50px' }),
				style({ opacity: 0, height: '0px' })])))
		]
		)],
})
export class DualListComponent implements OnInit {


	@Input() distributionTargets: Array<WorkbasketSummary>;
	@Output() distributionTargetsChange = new EventEmitter<Array<WorkbasketSummary>>();
	@Input() distributionTargetsSelected: Array<WorkbasketSummary>;
	@Output() performDualListFilter = new EventEmitter<{ filterBy: FilterModel, side: Side }>();
	@Input() requestInProgress = false;
	@Input() side: Side;
	@Input() header: string;

	sideNumber = 0;
	toggleDtl = false;
	toolbarState = false;

	constructor() { }

	ngOnInit() {
		this.sideNumber = this.side === Side.LEFT ? 0 : 1;
	}

	selectAll(selected: boolean) {
		this.distributionTargets.forEach((element: any) => {
			element.selected = selected;
		});
	}

	performAvailableFilter(filterModel: FilterModel) {
		this.performDualListFilter.emit({ filterBy: filterModel, side: this.side });
	}
}
