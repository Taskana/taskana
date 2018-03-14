import { Component, OnInit, Input } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketSummary } from '../../../model/workbasket-summary';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';
import { FilterModel } from '../../../shared/filter/filter.component'

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';

import { Subscription } from 'rxjs';
import { element } from 'protractor';
import { WorkbasketSummaryResource } from '../../../model/workbasket-summary-resource';

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
	distributionTargetsResource: WorkbasketSummaryResource;
	distributionTargetsLeft: Array<WorkbasketSummary> = [];
	distributionTargetsRight: Array<WorkbasketSummary> = [];
	distributionTargetsSelected: Array<WorkbasketSummary> = [];


	filterBy: FilterModel = new FilterModel();
	requestInProgress: boolean = false;
	requestInProgressLeft: boolean = false;
	requestInProgressRight: boolean = false;

	constructor(private workbasketService: WorkbasketService) { }

	ngOnInit() {
		this.requestInProgressLeft = true;
		this.requestInProgressRight = true;
		this.distributionTargetsSubscription = this.workbasketService.getWorkBasketsDistributionTargets(this.workbasket._links.distributionTargets.href).subscribe((distributionTargetsSelectedResource: WorkbasketSummaryResource) => {
				this.distributionTargetsSelected = distributionTargetsSelectedResource._embedded ? distributionTargetsSelectedResource._embedded.workbaskets :[];
				this.workbasketSubscription = this.workbasketService.getWorkBasketsSummary().subscribe((distributionTargetsAvailable: WorkbasketSummaryResource) => {
					this.distributionTargetsResource = distributionTargetsAvailable;
					this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
					this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
					this.requestInProgressLeft = false;
					this.requestInProgressRight = false;
				});
		})
	}

	selectAll(side: number, selected: boolean) {
		if (side === 0) {
			this.distributionTargetsLeft.forEach((element: any) => {
				element.selected = selected;
			});
		}
		else if (side === 1) {
			this.distributionTargetsRight.forEach((element: any) => {
				element.selected = selected;
			});
		}
	}

	moveDistributionTargets(side: number) {
		if (side === 0) {
			let itemsSelected = this.getSelectedItems(this.distributionTargetsLeft, this.distributionTargetsRight)
			this.distributionTargetsSelected = this.distributionTargetsSelected.concat(itemsSelected);
			this.distributionTargetsRight = this.distributionTargetsRight.concat(itemsSelected);
		}
		else if (side === 1) {
			let itemsSelected = this.getSelectedItems(this.distributionTargetsRight, this.distributionTargetsLeft);
			this.distributionTargetsSelected = this.removeSeletedItems(this.distributionTargetsSelected, itemsSelected);
			this.distributionTargetsRight = this.removeSeletedItems(this.distributionTargetsRight, itemsSelected);
			this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
		}
	}

	performAvailableFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performFilter(0);
	}
	performSelectedFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performFilter(1);
	}

	private performFilter(listType: number) {

		listType ? this.distributionTargetsRight = undefined : this.distributionTargetsLeft = undefined;
		listType ? this.requestInProgressRight = true : this.requestInProgressLeft = true;
		this.workbasketFilterSubscription = this.workbasketService.getWorkBasketsSummary(true, undefined, undefined, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key).subscribe((resultList: WorkbasketSummaryResource) => {
				listType ? this.distributionTargetsRight = resultList._embedded.workbaskets : this.distributionTargetsLeft = resultList._embedded.workbaskets;
				listType ? this.requestInProgressRight = false : this.requestInProgressLeft = false;
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

	private ngOnDestroy(): void {
		if (this.distributionTargetsSubscription) { this.distributionTargetsSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.workbasketFilterSubscription) { this.workbasketFilterSubscription.unsubscribe(); }
	}

}

