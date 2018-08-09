import { Component, Input, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs';
import { FormBuilder, Validators, FormArray } from '@angular/forms';

import { Workbasket } from 'app/models/workbasket';
import { WorkbasketAccessItems } from 'app/models/workbasket-access-items';
import { WorkbasketAccessItemsResource } from 'app/models/workbasket-access-items-resource';
import { ErrorModel } from 'app/models/modal-error';
import { ACTION } from 'app/models/action';
import { AlertModel, AlertType } from 'app/models/alert';

import { SavingWorkbasketService, SavingInformation } from 'app/administration/services/saving-workbaskets/saving-workbaskets.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { highlight } from 'app/shared/animations/validation.animation';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';

declare const $: any;
@Component({
	selector: 'taskana-workbasket-access-items',
	templateUrl: './access-items.component.html',
	animations: [highlight],
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

	accessIdField = this.customFieldService.getCustomField('Owner', 'workbaskets.access-items.accessId');
	custom1Field = this.customFieldService.getCustomField('Custom 1', 'workbaskets.access-items.custom1');
	custom2Field = this.customFieldService.getCustomField('Custom 2', 'workbaskets.access-items.custom2');
	custom3Field = this.customFieldService.getCustomField('Custom 3', 'workbaskets.access-items.custom3');
	custom4Field = this.customFieldService.getCustomField('Custom 4', 'workbaskets.access-items.custom4');
	custom5Field = this.customFieldService.getCustomField('Custom 5', 'workbaskets.access-items.custom5');
	custom6Field = this.customFieldService.getCustomField('Custom 6', 'workbaskets.access-items.custom6');
	custom7Field = this.customFieldService.getCustomField('Custom 7', 'workbaskets.access-items.custom7');
	custom8Field = this.customFieldService.getCustomField('Custom 8', 'workbaskets.access-items.custom8');
	custom9Field = this.customFieldService.getCustomField('Custom 9', 'workbaskets.access-items.custom9');
	custom10Field = this.customFieldService.getCustomField('Custom 10', 'workbaskets.access-items.custom10');
	custom11Field = this.customFieldService.getCustomField('Custom 11', 'workbaskets.access-items.custom11');
	custom12Field = this.customFieldService.getCustomField('Custom 12', 'workbaskets.access-items.custom12');

	accessItemsResource: WorkbasketAccessItemsResource;
	accessItemsClone: Array<WorkbasketAccessItems>;
	accessItemsResetClone: Array<WorkbasketAccessItems>;
	requestInProgress = false;
	modalTitle: string;
	modalErrorMessage: string;
	accessItemsubscription: Subscription;
	savingAccessItemsSubscription: Subscription;
	AccessItemsForm = this.formBuilder.group({
		accessItemsGroups: this.formBuilder.array([
		])
	});
	toogleValidationAccessIdMap = new Map<number, boolean>();
	private initialized = false;

	setAccessItemsGroups(accessItems: Array<WorkbasketAccessItems>) {
		const AccessItemsFormGroups = accessItems.map(accessItem => this.formBuilder.group(accessItem));
		AccessItemsFormGroups.map(accessItemGroup => {
      accessItemGroup.controls['accessId'].setValidators(Validators.required);
		});
		const AccessItemsFormArray = this.formBuilder.array(AccessItemsFormGroups);
		this.AccessItemsForm.setControl('accessItemsGroups', AccessItemsFormArray);
	};

	get accessItemsGroups(): FormArray {
		return this.AccessItemsForm.get('accessItemsGroups') as FormArray;
	};

	constructor(
		private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private errorModalService: ErrorModalService,
		private savingWorkbaskets: SavingWorkbasketService,
		private requestInProgressService: RequestInProgressService,
		private customFieldService: CustomFieldsService,
		private formBuilder: FormBuilder,
    private formsValidatorService: FormsValidatorService) {
	}

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
				this.setAccessItemsGroups(accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : []);
				this.accessItemsClone = this.cloneAccessItems(accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : []);
				this.accessItemsResetClone = this.cloneAccessItems(accessItemsResource._embedded ? accessItemsResource._embedded.accessItems : []);
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
		const newForm = this.formBuilder.group(
			new WorkbasketAccessItems(undefined, this.workbasket.workbasketId, '', true));
		newForm.controls['accessId'].setValidators(Validators.required);
		this.accessItemsGroups.push(newForm);
		this.accessItemsClone.push(new WorkbasketAccessItems(undefined, this.workbasket.workbasketId, '', true));
	}

	clear() {
		this.formsValidatorService.formSubmitAttempt = false;
		this.AccessItemsForm.reset();
		this.setAccessItemsGroups(this.accessItemsResetClone);
		this.accessItemsClone = this.cloneAccessItems(this.accessItemsResetClone);
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
	}

	remove(index: number) {
		this.accessItemsGroups.removeAt(index);
		this.accessItemsClone.splice(index, 1);
	}

	isFieldValid(field: string, index: number): boolean {
		return this.formsValidatorService.isFieldValid(this.accessItemsGroups[index], field);
	}

	onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormAccess(this.accessItemsGroups, this.toogleValidationAccessIdMap).then(value => {
      if (value) {
        this.onSave();
      }
    });
	}

	checkAll(row: number, value: any) {
		const checkAll = value.target.checked;
		const workbasketAccessItemsObj = new WorkbasketAccessItems();
		for (const property in workbasketAccessItemsObj) {
			if (property !== 'accessId' && property !== '_links' && property !== 'workbasketId' && property !== 'accessItemId') {
				this.accessItemsGroups.controls[row].get(property).setValue(checkAll);
			}
		}
	}

	private onSave() {
		this.requestInProgressService.setRequestInProgress(true);
		this.workbasketService.updateWorkBasketAccessItem(this.accessItemsResource._links.self.href, this.AccessItemsForm.value.accessItemsGroups)
			.subscribe(response => {
				this.accessItemsClone = this.cloneAccessItems(this.AccessItemsForm.value.accessItemsGroups);
				this.accessItemsResetClone = this.cloneAccessItems(this.AccessItemsForm.value.accessItemsGroups);
				this.alertService.triggerAlert(new AlertModel(
					AlertType.SUCCESS, `Workbasket  ${this.workbasket.name} Access items were saved successfully`));
				this.requestInProgressService.setRequestInProgress(false);
			}, error => {
				this.errorModalService.triggerError(new ErrorModel(`There was error while saving your workbasket's access items`, error))
				this.requestInProgressService.setRequestInProgress(false);
			})
	}

	private setBadge() {
		if (this.action === ACTION.COPY) {
			this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
		}
	}

	private cloneAccessItems(inputaccessItem): Array<WorkbasketAccessItems> {
		return this.AccessItemsForm.value.accessItemsGroups.map(
			(accessItems: WorkbasketAccessItems) => Object.assign({}, accessItems)
		);
	}
	private setWorkbasketIdForCopy(workbasketId: string) {
		this.accessItemsGroups.value.forEach(element => {
			element.accessItemId = undefined;
			element.workbasketId = workbasketId;
		});
	}

	ngOnDestroy(): void {
		if (this.accessItemsubscription) { this.accessItemsubscription.unsubscribe(); }
		if (this.savingAccessItemsSubscription) { this.savingAccessItemsSubscription.unsubscribe(); }
	}
}
