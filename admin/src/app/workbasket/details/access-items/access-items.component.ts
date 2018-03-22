import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { Workbasket } from '../../../model/workbasket';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';
import { WorkbasketAccessItemsResource } from '../../../model/workbasket-access-items-resource';
import { ErrorModalService } from '../../../services/error-modal.service';
import { ErrorModel } from '../../../model/modal-error';
import { SavingWorkbasketService, SavingInformation } from '../../../services/saving-workbaskets/saving-workbaskets.service';
import { ACTION } from '../../../model/action';

declare var $: any;

@Component({
	selector: 'taskana-workbasket-access-items',
	templateUrl: './access-items.component.html',
	styleUrls: ['./access-items.component.scss']
})
export class AccessItemsComponent implements OnInit, OnDestroy {

	@Input()
	workbasket: Workbasket;
	@Input()
	action: string;
	badgeMessage = '';

	accessItemsResource: WorkbasketAccessItemsResource;
	accessItems: Array<WorkbasketAccessItems>;
	accessItemsClone: Array<WorkbasketAccessItems>;
	accessItemsResetClone: Array<WorkbasketAccessItems>;
	requestInProgress = false;
	modalSpinner = true;
	modalTitle: string;
	modalErrorMessage: string;
	accessItemsubscription: Subscription;
	savingAccessItemsSubscription: Subscription;


	constructor(
		private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private errorModalService: ErrorModalService,
		private savingWorkbaskets: SavingWorkbasketService) { }

	ngOnInit() {
		if (!this.workbasket._links.accessItems) {
			return;
		}
		this.accessItemsubscription = this.workbasketService.getWorkBasketAccessItems(this.workbasket._links.accessItems.href)
			.subscribe((accessItemsResource: WorkbasketAccessItemsResource) => {
				this.accessItemsResource = accessItemsResource;
				this.accessItems = accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : [];
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
			})
		this.savingAccessItemsSubscription = this.savingWorkbaskets.triggeredAccessItemsSaving()
			.subscribe((savingInformation: SavingInformation) => {
				if (this.action === ACTION.COPY) {
					this.accessItemsResource._links.self.href = savingInformation.url;
					this.setWorkbasketIdForCopy(savingInformation.workbasketId);
					this.onSave();
				}
			})
		if (this.action === ACTION.COPY) {
			this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
		}
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
		this.requestInProgress = true;
		this.workbasketService.updateWorkBasketAccessItem(this.accessItemsResource._links.self.href, this.accessItems)
			.subscribe(response => {
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
				this.alertService.triggerAlert(new AlertModel(
					AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
				this.requestInProgress = false;
				return true;
			}, error => {
				this.errorModalService.triggerError(new ErrorModel(`There was error while saving your workbasket's access items`, error))
				this.requestInProgress = false;
				return false;
			})
		return false;
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
