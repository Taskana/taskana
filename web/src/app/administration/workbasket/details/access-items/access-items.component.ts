import { Component, OnInit, Input, AfterViewInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { Workbasket } from 'app/models/workbasket';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { ErrorModel } from 'app/models/modal-error';
import { ACTION } from 'app/models/action';
import { AlertModel, AlertType } from 'app/models/alert';

import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { WorkbasketService } from 'app/administration/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';

declare var $: any;

@Component({
	selector: 'taskana-workbasket-access-items',
	templateUrl: './access-items.component.html',
	styleUrls: ['./access-items.component.scss']
})
export class AccessItemsComponent implements OnChanges, OnDestroy {


	@Input()
	workbasket: Workbasket;
	@Input()
	action: string;
	@Input()
	active: string;
	badgeMessage = '';

	accessItemsResource: WorkbasketAccessItemsResource;
	accessItems: Array<WorkbasketAccessItems>;
	accessItemsClone: Array<WorkbasketAccessItems>;
	accessItemsResetClone: Array<WorkbasketAccessItems>;
	requestInProgress = false;
	modalTitle: string;
	modalErrorMessage: string;
	accessItemsubscription: Subscription;
	savingAccessItemsSubscription: Subscription;
	private initialized = false;


	constructor(
		private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private errorModalService: ErrorModalService,
		private savingWorkbaskets: SavingWorkbasketService,
		private requestInProgressService: RequestInProgressService) { }

	ngOnChanges(changes: SimpleChanges): void {
		if (!this.initialized && changes.active && changes.active.currentValue === 'accessItems') {
			this.init();
		}
		if (changes.action) {
			this.setBadge();
		}
	}
	private init() {
		this.initialized = true;
		if (!this.workbasket._links.accessItems) {
			return;
		}
		this.requestInProgress = true;
		this.accessItemsubscription = this.workbasketService.getWorkBasketAccessItems(this.workbasket._links.accessItems.href)
			.subscribe((accessItemsResource: WorkbasketAccessItemsResource) => {
				this.accessItemsResource = accessItemsResource;
				this.accessItems = accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : [];
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
				this.requestInProgress = false;
			})
		this.savingAccessItemsSubscription = this.savingWorkbaskets.triggeredAccessItemsSaving()
			.subscribe((savingInformation: SavingInformation) => {
				if (this.action === ACTION.COPY) {
					this.accessItemsResource._links.self.href = savingInformation.url;
					this.setWorkbasketIdForCopy(savingInformation.workbasketId);
					this.onSave();
				}
			})

	}

	addAccessItem() {
		this.accessItems.push(new WorkbasketAccessItems(undefined, this.workbasket.workbasketId, undefined, true));
		this.accessItemsClone.push(new WorkbasketAccessItems());
	}

	clear() {
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
		this.accessItems = this.cloneAccessItems(this.accessItemsResetClone);
		this.accessItemsClone = this.cloneAccessItems(this.accessItemsResetClone);
	}

	remove(index: number) {
		this.accessItems.splice(index, 1);
		this.accessItemsClone.splice(index, 1);
	}

	onSave(): boolean {
		this.requestInProgressService.setRequestInProgress(true);
		this.workbasketService.updateWorkBasketAccessItem(this.accessItemsResource._links.self.href, this.accessItems)
			.subscribe(response => {
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
				this.alertService.triggerAlert(new AlertModel(
					AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
					this.requestInProgressService.setRequestInProgress(false);
				return true;
			}, error => {
				this.errorModalService.triggerError(new ErrorModel(`There was error while saving your workbasket's access items`, error))
				this.requestInProgressService.setRequestInProgress(false);
				return false;
			})
		return false;
	}
	private setBadge() {
		if (this.action === ACTION.COPY) {
			this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
		}
	}

	private cloneAccessItems(inputaccessItem): Array<WorkbasketAccessItems> {
		const accessItemClone = new Array<WorkbasketAccessItems>();
		inputaccessItem.forEach(accessItem => {
			accessItemClone.push({ ...accessItem });
		});
		return accessItemClone;
	}
	private setWorkbasketIdForCopy(workbasketId: string) {
		this.accessItems.forEach(element => {
			element.accessItemId = undefined;
			element.workbasketId = workbasketId;
		});
	}

	ngOnDestroy(): void {
		if (this.accessItemsubscription) { this.accessItemsubscription.unsubscribe(); }
		if (this.savingAccessItemsSubscription) { this.savingAccessItemsSubscription.unsubscribe(); }
	}
}
