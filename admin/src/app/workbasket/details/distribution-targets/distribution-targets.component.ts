import { Component, OnInit, Input } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketSummary } from '../../../model/workbasketSummary';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';
import { FilterModel } from '../../../shared/filter/filter.component'
import { TREE_ACTIONS, KEYS, IActionMapping, ITreeOptions } from 'angular-tree-component';

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';

import { Subscription } from 'rxjs';
import { element } from 'protractor';


export enum Side {
	LEFT,
	RIGHT
}
@Component({
	selector: 'taskana-workbaskets-distribution-targets',
	templateUrl: './distribution-targets.component.html',
	styleUrls: ['./distribution-targets.component.scss']
})
export class DistributionTargetsComponent implements OnInit {

	@Input()
	workbasket: Workbasket;

	distributionTargetsSubscription: Subscription;
	workbasketSubscription: Subscription;
	workbasketFilterSubscription: Subscription;

	distributionTargetsLeft: Array<WorkbasketSummary>;
	distributionTargetsRight: Array<WorkbasketSummary>;
	distributionTargetsSelected: Array<WorkbasketSummary>;
	distributionTargetsClone: Array<WorkbasketSummary>;
	distributionTargetsSelectedClone: Array<WorkbasketSummary>;


	filterBy: FilterModel = new FilterModel();
	requestInProgress: boolean = false;
	requestInProgressLeft: boolean = false;
	requestInProgressRight: boolean = false;
	side = Side;

	constructor(private workbasketService: WorkbasketService) { }

	ngOnInit() {
		this.onRequest(undefined);
		this.distributionTargetsSubscription = this.workbasketService.getWorkBasketsDistributionTargets(this.workbasket.workbasketId).subscribe((distributionTargetsSelected: Array<WorkbasketSummary>) => {
			this.distributionTargetsSelected = distributionTargetsSelected;
			this.distributionTargetsClone = Object.assign([], distributionTargetsSelected);
			this.workbasketSubscription = this.workbasketService.getWorkBasketsSummary().subscribe((distributionTargetsAvailable: Array<WorkbasketSummary>) => {
				this.distributionTargetsLeft = distributionTargetsAvailable;
				this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable);
				this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable);
				this.onRequest(undefined, true);
			});
		})
	}

	selectAll(side: Side, selected: boolean) {
		if (side === Side.LEFT) {
			this.distributionTargetsLeft.forEach((element: any) => {
				element.selected = selected;
			});
		}
		else {
			this.distributionTargetsRight.forEach((element: any) => {
				element.selected = selected;
			});
		}
	}

	moveDistributionTargets(side: number) {
		if (side === Side.LEFT) {
			let itemsSelected = this.getSelectedItems(this.distributionTargetsLeft, this.distributionTargetsRight)
			this.distributionTargetsSelected = this.distributionTargetsSelected.concat(itemsSelected);
			this.distributionTargetsRight = this.distributionTargetsRight.concat(itemsSelected);
		}
		else {
			let itemsSelected = this.getSelectedItems(this.distributionTargetsRight, this.distributionTargetsLeft);
			this.distributionTargetsSelected = this.removeSeletedItems(this.distributionTargetsSelected, itemsSelected);
			this.distributionTargetsRight = this.removeSeletedItems(this.distributionTargetsRight, itemsSelected);
			this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
		}
	}

	performAvailableFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performFilter(Side.LEFT);
	}
	performSelectedFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performFilter(Side.RIGHT);
	}

	onSave() {

	}

	onClear() {
		this.distributionTargetsLeft = Object.assign([], this.distributionTargetsClone);
		this.distributionTargetsRight = Object.assign([], this.distributionTargetsSelectedClone);
		this.distributionTargetsSelected = Object.assign([], this.distributionTargetsSelectedClone);
	}

	private performFilter(side: Side) {

		side === Side.RIGHT ? this.distributionTargetsRight = undefined : this.distributionTargetsLeft = undefined;
		this.onRequest(side, false);
		this.workbasketFilterSubscription = this.workbasketService.getWorkBasketsSummary(true, undefined, undefined, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key).subscribe(resultList => {
				side === Side.RIGHT ? this.distributionTargetsRight = resultList : this.distributionTargetsLeft = resultList;
				this.onRequest(side, true);
			});
	}

	private getSelectedItems(originList: any, destinationList: any): Array<any> {
		return originList.filter((element: any) => { return (element.selected === true) });
	}

	private removeSeletedItems(originList: any, selectedItemList) {
		for (let index = originList.length - 1; index >= 0; index--) {
			if (selectedItemList.some(elementToRemove => { return originList[index].workbasketId === elementToRemove.workbasketId })) {
				originList.splice(index, 1);
			}
		}
		return originList;
	}

	private onRequest(side: Side = undefined, finished: boolean = false) {
		if (finished) {
			side === undefined ? (this.requestInProgressLeft = false, this.requestInProgressRight = false) :
				side === Side.LEFT ? this.requestInProgressLeft = false : this.requestInProgressRight = false;
			return;
		}
		side === undefined ? (this.requestInProgressLeft = true, this.requestInProgressRight = true) :
			side === Side.LEFT ? this.requestInProgressLeft = true : this.requestInProgressRight = true;
	}

	private ngOnDestroy(): void {
		if (this.distributionTargetsSubscription) { this.distributionTargetsSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.workbasketFilterSubscription) { this.workbasketFilterSubscription.unsubscribe(); }
	}

}

