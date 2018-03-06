import { Component, OnInit, Input, AfterViewInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Utils } from '../../../shared/utils/utils';

import { Workbasket } from '../../../model/workbasket';
import { WorkbasketAccessItems } from '../../../model/workbasket-access-items';

import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';

declare var $: any;

@Component({
	selector: 'taskana-workbasket-access-items',
	templateUrl: './access-items.component.html',
	styleUrls: ['./access-items.component.scss']
})
export class AccessItemsComponent implements OnInit {

	@Input()
	workbasket: Workbasket;

	accessItems: Array<WorkbasketAccessItems>;
	accessItemsClone: Array<WorkbasketAccessItems>;
	accessItemsResetClone: Array<WorkbasketAccessItems>;
	requestInProgress: boolean = false;
	modalSpinner: boolean = true;
	modalTitle: string;
	modalErrorMessage: string;
	accessItemsubscription: Subscription;
	

	constructor(private workbasketService: WorkbasketService, private alertService: AlertService) { }

	ngOnInit() {
		this.accessItemsubscription = this.workbasketService.getWorkBasketAccessItems(this.workbasket.workbasketId).subscribe( (accessItems: Array<WorkbasketAccessItems>) =>{
			this.accessItems = accessItems;
			this.accessItemsClone = this.cloneAccessItems(this.accessItems);
			this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
		})
		
	}

	addAccessItem() {
		this.accessItems.push(new WorkbasketAccessItems(undefined,this.workbasket.workbasketId, undefined, true));
		this.accessItemsClone.push(new WorkbasketAccessItems());
	}

	clear() {
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
		this.accessItems = this.cloneAccessItems(this.accessItemsResetClone);
		this.accessItemsClone = this.cloneAccessItems(this.accessItemsResetClone);
	}

	remove(index: number) {
		this.accessItems.splice(index,1);
		this.accessItemsClone.splice(index,1);
	}

	onSave(): boolean {
		if(!this.accessItems[0].links){
			return;
		}

		this.workbasketService.updateWorkBasketAccessItem(Utils.getTagLinkRef(this.accessItems[0].links, 'setWorkbasketAccessItems').href, this.accessItems).subscribe(response =>{
			this.accessItemsClone = this.cloneAccessItems(this.accessItems);
			this.accessItemsResetClone = this.cloneAccessItems(this.accessItems);
			this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
			return true;
		},
		error => {
			this.modalErrorMessage = error.message;
			return false;
		})
	}

	setValue() { debugger; }

	private cloneAccessItems(inputaccessItem): Array<WorkbasketAccessItems>{
		let accessItemClone = new Array<WorkbasketAccessItems>();
		inputaccessItem.forEach(accessItem => {
			accessItemClone.push({... accessItem});
		});
		return accessItemClone;
	}

	private ngOnDestroy(): void {
		if (this.accessItemsubscription) { this.accessItemsubscription.unsubscribe(); }
	}
}
