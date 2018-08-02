import { Component, Input, OnDestroy, SimpleChanges, OnChanges } from '@angular/core';
import { Subscription } from 'rxjs';

import { Workbasket } from 'app/models/workbasket';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketDistributionTargetsResource } from 'app/models/workbasket-distribution-targets-resource';
import { ErrorModel } from 'app/models/modal-error';
import { ACTION } from 'app/models/action';
import { AlertModel, AlertType } from 'app/models/alert';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

export enum Side {
	LEFT,
	RIGHT
}
@Component({
	selector: 'taskana-workbaskets-distribution-targets',
	templateUrl: './distribution-targets.component.html',
	styleUrls: ['./distribution-targets.component.scss']
})
export class DistributionTargetsComponent implements OnChanges, OnDestroy {

	@Input()
	workbasket: Workbasket;
	@Input()
	action: string;
	@Input()
	active: string;
	badgeMessage = '';

	distributionTargetsSubscription: Subscription;
	workbasketSubscription: Subscription;
	workbasketFilterSubscription: Subscription;
	savingDistributionTargetsSubscription: Subscription;

	distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource;
	distributionTargetsLeft: Array<WorkbasketSummary>;
	distributionTargetsRight: Array<WorkbasketSummary>;
	distributionTargetsSelected: Array<WorkbasketSummary>;
	distributionTargetsClone: Array<WorkbasketSummary>;
	distributionTargetsSelectedClone: Array<WorkbasketSummary>;

	requestInProgressLeft = false;
	requestInProgressRight = false;
	modalErrorMessage: string;
	side = Side;
	private initialized = false;

	constructor(
		private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private savingWorkbaskets: SavingWorkbasketService,
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService) { }

	ngOnChanges(changes: SimpleChanges): void {
		if (!this.initialized && changes.active && changes.active.currentValue === 'distributionTargets') {
			this.init();
		}
		if (changes.action) {
			this.setBadge();
		}
	}

	private init() {
		this.initialized = true;
		this.onRequest(undefined);
		if (!this.workbasket._links.distributionTargets) {
			return;
		}
		this.distributionTargetsSubscription = this.workbasketService.getWorkBasketsDistributionTargets(
			this.workbasket._links.distributionTargets.href).subscribe(
				(distributionTargetsSelectedResource: WorkbasketDistributionTargetsResource) => {
					this.distributionTargetsSelectedResource = distributionTargetsSelectedResource;
					this.distributionTargetsSelected = distributionTargetsSelectedResource._embedded ?
						distributionTargetsSelectedResource._embedded.distributionTargets : [];
					this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
					this.workbasketSubscription = this.workbasketService.getWorkBasketsSummary(true,
						undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined,
						undefined, undefined, undefined, true).subscribe(
							(distributionTargetsAvailable: WorkbasketSummaryResource) => {
								this.distributionTargetsLeft = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
								this.distributionTargetsRight = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
								this.distributionTargetsClone = Object.assign([], distributionTargetsAvailable._embedded.workbaskets);
								this.onRequest(undefined, true);
							});
				});

		this.savingDistributionTargetsSubscription = this.savingWorkbaskets.triggeredDistributionTargetsSaving()
			.subscribe((savingInformation: SavingInformation) => {
				if (this.action === ACTION.COPY) {
					this.distributionTargetsSelectedResource._links.self.href = savingInformation.url;
					this.onSave();
				}
			});

	}

	moveDistributionTargets(side: number) {
		if (side === Side.LEFT) {
			const itemsSelected = this.getSelectedItems(this.distributionTargetsLeft, this.distributionTargetsRight)
			this.distributionTargetsSelected = this.distributionTargetsSelected.concat(itemsSelected);
			this.distributionTargetsRight = this.distributionTargetsRight.concat(itemsSelected);
		} else {
			const itemsSelected = this.getSelectedItems(this.distributionTargetsRight, this.distributionTargetsLeft);
			this.distributionTargetsSelected = this.removeSeletedItems(this.distributionTargetsSelected, itemsSelected);
			this.distributionTargetsRight = this.removeSeletedItems(this.distributionTargetsRight, itemsSelected);
			this.distributionTargetsLeft = this.distributionTargetsLeft.concat(itemsSelected);
		}
	}

	onSave() {
		this.requestInProgressService.setRequestInProgress(true);
		this.workbasketService.updateWorkBasketsDistributionTargets(
			this.distributionTargetsSelectedResource._links.self.href, this.getSeletedIds()).subscribe(response => {
				this.requestInProgressService.setRequestInProgress(false);
				this.distributionTargetsSelected = response._embedded ? response._embedded.distributionTargets : [];
				this.distributionTargetsSelectedClone = Object.assign([], this.distributionTargetsSelected);
				this.distributionTargetsClone = Object.assign([], this.distributionTargetsLeft);
				this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS,
					`Workbasket  ${this.workbasket.name} : Distribution targets were saved successfully`));
				return true;
			},
				error => {
					this.errorModalService.triggerError(new ErrorModel(`There was error while saving your workbasket's distribution targets`, error))
					this.requestInProgressService.setRequestInProgress(false);
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

	performFilter(dualListFilter: any) {
		dualListFilter.side === Side.RIGHT ? this.distributionTargetsRight = undefined : this.distributionTargetsLeft = undefined;
		this.onRequest(dualListFilter.side, false);
		this.workbasketFilterSubscription = this.workbasketService.getWorkBasketsSummary(true, undefined, undefined, undefined,
      dualListFilter.filterBy.filterParams.name, dualListFilter.filterBy.filterParams.description, undefined,
      dualListFilter.filterBy.filterParams.owner,	dualListFilter.filterBy.filterParams.type, undefined,
      dualListFilter.filterBy.filterParams.key, undefined, true).subscribe(resultList => {
				(dualListFilter.side === Side.RIGHT) ?
					this.distributionTargetsRight = (resultList._embedded ? resultList._embedded.workbaskets : []) :
					this.distributionTargetsLeft = (resultList._embedded ? resultList._embedded.workbaskets : []);
				this.onRequest(dualListFilter.side, true);
			});
	}

	private setBadge() {
		if (this.action === ACTION.COPY) {
			this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
		}
	}

	private getSelectedItems(originList: any, destinationList: any): Array<any> {
		return originList.filter((item: any) => { return (item.selected === true) });
	}

	private removeSeletedItems(originList: any, selectedItemList) {
		for (let index = originList.length - 1; index >= 0; index--) {
			if (selectedItemList.some(itemToRemove => { return originList[index].workbasketId === itemToRemove.workbasketId })) {
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
		const distributionTargetsSelelected: Array<string> = [];
		this.distributionTargetsSelected.forEach(item => {
			distributionTargetsSelelected.push(item.workbasketId);
		})
		return distributionTargetsSelelected;
	}

	ngOnDestroy(): void {
		if (this.distributionTargetsSubscription) { this.distributionTargetsSubscription.unsubscribe(); }
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.workbasketFilterSubscription) { this.workbasketFilterSubscription.unsubscribe(); }
		if (this.savingDistributionTargetsSubscription) { this.savingDistributionTargetsSubscription.unsubscribe(); }

	}

}

