import { Component, OnInit, Input } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketSummary } from '../../../model/workbasket-summary';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';
import { FilterModel } from '../../../shared/filter/filter.component'
import { TREE_ACTIONS, KEYS, IActionMapping, ITreeOptions } from 'angular-tree-component';

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';

import { Subscription } from 'rxjs';
import { element } from 'protractor';
import { WorkbasketSummaryResource } from '../../../model/workbasket-summary-resource';
import { WorkbasketDistributionTargetsResource } from '../../../model/workbasket-distribution-targets-resource';

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

	distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource;
	distributionTargetsLeft: Array<WorkbasketSummary>;
	distributionTargetsRight: Array<WorkbasketSummary>;
	distributionTargetsSelected: Array<WorkbasketSummary>;
	distributionTargetsClone: Array<WorkbasketSummary>;
	distributionTargetsSelectedClone: Array<WorkbasketSummary>;


	requestInProgress: boolean = false;
	requestInProgressLeft: boolean = false;
	requestInProgressRight: boolean = false;
	modalErrorMessage: string;
	side = Side;

	constructor(private workbasketService: WorkbasketService, private alertService: AlertService) { }

	ngOnInit() {
		this.onRequest(undefined);
		this.distributionTargetsSubscription = this.workbasketService.getWorkBasketsDistributionTargets(this.workbasket._links.distributionTargets.href).subscribe((distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource) => {
			this.distributionTargetsSelectedResource = distributionTargetsSelectedResource;
			this.distributionTargetsSelected = distributionTargetsSelectedResource._embedded ? distributionTargetsSelectedResource._embedded.distributionTargets : [];
			this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
			this.workbasketSubscription = this.workbasketService.getWorkBasketsSummary().subscribe((distributionTargetsAvailable: WorkbasketSummaryResource) => {
				this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
				this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
				this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
				this.onRequest(undefined, true);
			});
		});
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

	onSave() {
		this.requestInProgress = true;
		this.workbasketService.updateWorkBasketsDistributionTargets(this.distributionTargetsSelectedResource._links.self.href, this.getSeletedIds()).subscribe(response => {
			this.requestInProgress = false;
			this.distributionTargetsSelected = response._embedded ? response._embedded.distributionTargets : [];
			this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
			this.distributionTargetsClone = Object.assign([], this.distributionTargetsLeft);
			this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
			return true;
		},
			error => {
				this.modalErrorMessage = error.message;
				this.requestInProgress = false;
				return false;
			}
		)
		return false;

	}

	onClear() {
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
		this.distributionTargetsLeft = Object.assign([], this.distributionTargetsClone);
		this.distributionTargetsRight = Object.assign([], this.distributionTargetsSelectedClone);
		this.distributionTargetsSelected = Object.assign([], this.distributionTargetsSelectedClone);
	}

	requestTimeoutExceeded(message: string) {
		this.modalErrorMessage = message;
	}

	performFilter(dualListFilter: any) {

		dualListFilter.side === Side.RIGHT ? this.distributionTargetsRight = undefined : this.distributionTargetsLeft = undefined;
		this.onRequest(dualListFilter.side, false);
		this.workbasketFilterSubscription = this.workbasketService.getWorkBasketsSummary(true, undefined, undefined, undefined,
			dualListFilter.filterBy.name, dualListFilter.filterBy.description, undefined, dualListFilter.filterBy.owner,
			dualListFilter.filterBy.type, undefined, dualListFilter.filterBy.key).subscribe(resultList => {
				(dualListFilter.side === Side.RIGHT) ?
					this.distributionTargetsRight = (resultList._embedded ? resultList._embedded.workbaskets : []) :
					this.distributionTargetsLeft = (resultList._embedded ? resultList._embedded.workbaskets : []);
				this.onRequest(dualListFilter.side, true);
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

	private getSeletedIds(): Array<string> {
		let distributionTargetsSelelected: Array<string> = [];
		this.distributionTargetsSelected.forEach(element => {
			distributionTargetsSelelected.push(element.workbasketId);
		})
		return distributionTargetsSelelected;
	}

	private ngOnDestroy(): void {
		if (this.distributionTargetsSubscription) { this.distributionTargetsSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.workbasketFilterSubscription) { this.workbasketFilterSubscription.unsubscribe(); }
	}

}

