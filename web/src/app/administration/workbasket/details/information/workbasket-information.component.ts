import { Component, OnInit, Input, Output, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { IconTypeComponent } from 'app/shared/type-icon/icon-type.component';
import { ICONTYPES } from 'app/models/type';
import { ErrorModel } from 'app/models/modal-error';
import { ACTION } from 'app/models/action';
import { Workbasket } from 'app/models/workbasket';

import { AlertService } from 'app/services/alert/alert.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { FormGroup } from '@angular/forms';
import { SavingWorkbasketService, SavingInformation } from 'app/services/saving-workbaskets/saving-workbaskets.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertModel, AlertType } from 'app/models/alert';

const dateFormat = 'yyyy-MM-ddTHH:mm:ss';
const dateLocale = 'en-US';
@Component({
	selector: 'taskana-workbasket-information',
	templateUrl: './workbasket-information.component.html',
	styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnChanges, OnDestroy {

	@Input()
	workbasket: Workbasket;
	workbasketClone: Workbasket;
	@Input()
	action: string;

	allTypes: Map<string, string>;
	requestInProgress = false;
	modalSpinner = false;
	hasChanges = false;
	badgeMessage = '';


	private workbasketSubscription: Subscription;
	private routeSubscription: Subscription;

	constructor(private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private route: ActivatedRoute,
		private router: Router,
		private errorModalService: ErrorModalService,
		private savingWorkbasket: SavingWorkbasketService) {
		this.allTypes = IconTypeComponent.allTypes;
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.workbasketClone = { ...this.workbasket };
		if (this.action === ACTION.CREATE) {
			this.badgeMessage = 'Creating new workbasket';
		} else if (this.action === ACTION.COPY) {
			this.badgeMessage = `Copying workbasket: ${this.workbasket.key}`;
		}

	}

	selectType(type: ICONTYPES) {
		this.workbasket.type = type;
	}

	onSave() {
		this.beforeRequest();
		if (!this.workbasket.workbasketId) {
			this.postNewWorkbasket();
			return true;
		}

		this.workbasketSubscription = (this.workbasketService.updateWorkbasket(this.workbasket._links.self.href, this.workbasket).subscribe(
			workbasketUpdated => {
				this.afterRequest();
				this.workbasket = workbasketUpdated;
				this.workbasketClone = { ...this.workbasket };
				this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Workbasket ${workbasketUpdated.key} was saved successfully`))
			},
			error => {
				this.afterRequest();
				this.errorModalService.triggerError(new ErrorModel('There was error while saving your workbasket', error))
			}
		));
	}

	onClear() {
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
		this.workbasket = { ...this.workbasketClone };
		this.hasChanges = false;
	}

	private beforeRequest() {
		this.requestInProgress = true;
		this.modalSpinner = true;
	}

	private afterRequest() {
		this.requestInProgress = false;
		this.workbasketService.triggerWorkBasketSaved();

	}

	private postNewWorkbasket() {
		this.addDateToWorkbasket();
		this.workbasketService.createWorkbasket(this.workbasket)
			.subscribe((workbasketUpdated: Workbasket) => {
				this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Workbasket ${workbasketUpdated.key} was created successfully`))
				this.workbasket = workbasketUpdated;
				this.afterRequest();
				this.workbasketService.triggerWorkBasketSaved();
				this.workbasketService.selectWorkBasket(this.workbasket.workbasketId);
				this.router.navigate(['../' + this.workbasket.workbasketId], { relativeTo: this.route });
				if (this.action === ACTION.COPY) {
					this.savingWorkbasket.triggerDistributionTargetSaving(
						new SavingInformation(this.workbasket._links.distributionTargets.href, this.workbasket.workbasketId));
					this.savingWorkbasket.triggerAccessItemsSaving(
						new SavingInformation(this.workbasket._links.accessItems.href, this.workbasket.workbasketId));
				}
			}, error => {
				this.errorModalService.triggerError(new ErrorModel('There was an error creating a workbasket',
					error.error ? error.error.message : error))
				this.requestInProgress = false;
			});
	}

	private addDateToWorkbasket() {
		const datePipe = new DatePipe(dateLocale);
		const date = datePipe.transform(Date.now(), dateFormat) + 'Z';
		this.workbasket.created = date;
		this.workbasket.modified = date;
	}

	ngOnDestroy() {
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
	}

}
