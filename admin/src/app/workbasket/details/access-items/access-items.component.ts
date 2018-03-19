import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { Workbasket } from '../../../model/workbasket';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';
import { WorkbasketAccessItemsResource } from '../../../model/workbasket-access-items-resource';

declare var $: any;

@Component({
	selector: 'taskana-workbasket-access-items',
	templateUrl: './access-items.component.html',
	styleUrls: ['./access-items.component.scss']
})
export class AccessItemsComponent implements OnInit, OnDestroy {

	@Input()
	workbasket: Workbasket;

	accessItemsResource: WorkbasketAccessItemsResource;
	accessItems: Array<WorkbasketAccessItems>;
	accessItemsClone: Array<WorkbasketAccessItems>;
	accessItemsResetClone: Array<WorkbasketAccessItems>;
	requestInProgress = false;
	modalSpinner = true;
	modalTitle: string;
	modalErrorMessage: string;
	accessItemsubscription: Subscription;


	constructor(private workbasketService: WorkbasketService, private alertService: AlertService) { }

	ngOnInit() {
		this.accessItemsubscription = this.workbasketService.getWorkBasketAccessItems(this.workbasket._links.accessItems.href)
			.subscribe((accessItemsResource: WorkbasketAccessItemsResource) => {
				this.accessItemsResource = accessItemsResource;
				this.accessItems = accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : [];
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
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
		this.requestInProgress = true;
		this.workbasketService.updateWorkBasketAccessItem(this.accessItemsResource._links.self.href, this.accessItems)
			.subscribe(response => {
				this.accessItemsClone = this.cloneAccessItems(this.accessItems);
				this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
				this.alertService.triggerAlert(new AlertModel(
					AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
				this.requestInProgress = false;
				return true;
			},
				error => {
					this.modalErrorMessage = error.message;
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

	ngOnDestroy(): void {
		if (this.accessItemsubscription) { this.accessItemsubscription.unsubscribe(); }
	}
}
