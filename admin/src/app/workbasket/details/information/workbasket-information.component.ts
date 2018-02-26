import { Component, OnInit, Input, Output } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketService } from '../../../services/workbasket.service';
import { IconTypeComponent, ICONTYPES } from '../../../shared/type-icon/icon-type.component';
import { Subscription } from 'rxjs';
import { Utils } from '../../../shared/utils/utils';
import { AlertService, AlertModel, AlertType } from '../../../services/alert.service';
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';

@Component({
	selector: 'workbasket-information',
	templateUrl: './workbasket-information.component.html',
	styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnInit {

	@Input()
	workbasket: Workbasket;
	workbasketClone: Workbasket;

	allTypes: Map<string, string>;
	requestInProgress: boolean = false;
	modalSpinner: boolean = false;
	modalErrorMessage: string;
	modalTitle: string = 'There was error while saving your workbasket';

	private workbasketSubscription: Subscription;
	private routeSubscription: Subscription;

	constructor(private workbasketService: WorkbasketService,
		private alertService: AlertService,
		private route: ActivatedRoute,
		private router: Router, ) {
		this.allTypes = IconTypeComponent.allTypes;
	}

	ngOnInit() {
		this.workbasketClone = { ...this.workbasket };
		this.routeSubscription = this.router.events.subscribe(event => {
			if (event instanceof NavigationStart) {
				this.checkForChanges();
			}
		});
	}

	selectType(type: ICONTYPES) {
		this.workbasket.type = type;
	}


	onSave() {
		this.beforeRequest();
		this.workbasketSubscription = (this.workbasketService.updateWorkbasket((Utils.getSelfRef(this.workbasket.links).href), this.workbasket).subscribe(
			workbasketUpdated => {
				this.afterRequest();
				this.workbasket = workbasketUpdated;
				this.workbasketClone = {...this.workbasket};
				this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Workbasket ${workbasketUpdated.key} was saved successfully`))
			},
			error => {
				this.afterRequest();
				this.modalErrorMessage = error;
			}
		));
	}

	onClear() {
		this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
		this.workbasket = { ...this.workbasketClone };
	}

	private beforeRequest(){
		this.requestInProgress = true;
		this.modalSpinner = true;
		this.modalErrorMessage = undefined;
	}
	private afterRequest(){
		this.requestInProgress = false;
		this.workbasketService.triggerWorkBasketSaved();
		
	}

	private checkForChanges() {
		if (!Workbasket.equals(this.workbasket, this.workbasketClone)) {
			this.openDiscardChangesModal();
		}
	}

	private openDiscardChangesModal() {

	}

	private ngOnDestroy() {
		if (this.workbasketSubscription) { this.workbasketSubscription.unsubscribe(); }
		if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
	}

}
