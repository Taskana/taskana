import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { SortingModel } from 'app/models/sorting';
import { FilterModel } from 'app/models/filter';
import { Subscription } from 'rxjs/Subscription';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { ErrorModel } from 'app/models/modal-error';
import { AlertModel, AlertType } from 'app/models/alert';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService} from 'app/services/alert/alert.service';

@Component({
	selector: 'taskana-workbasket-list-toolbar',
	templateUrl: './workbasket-list-toolbar.component.html',
	styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {


	@Input() workbaskets: Array<WorkbasketSummary>;
	@Input() workbasketIdSelected: string;
	@Input() workbasketIdSelectedChanged: string;
	@Output() performSorting = new EventEmitter<SortingModel>();
	@Output() performFilter = new EventEmitter<FilterModel>();
	workbasketServiceSubscription: Subscription;

	constructor(
		private workbasketService: WorkbasketService,
		private route: ActivatedRoute,
		private router: Router,
		private errorModalService: ErrorModalService,
		private requestInProgressService: RequestInProgressService,
		private alertService: AlertService) { }

	ngOnInit() {
	}

	sorting(sort: SortingModel) {
		this.performSorting.emit(sort);
	}

	filtering(filterBy: FilterModel) {
		this.performFilter.emit(filterBy);
	}

	addWorkbasket() {
		this.router.navigate([{ outlets: { detail: ['new-workbasket'] } }], { relativeTo: this.route });
	}

	removeWorkbasket() {
		this.requestInProgressService.setRequestInProgress(true);
		this.workbasketService.deleteWorkbasket(this.findWorkbasketSelectedObject()._links.self.href).subscribe(response => {
			this.requestInProgressService.setRequestInProgress(false);
			this.workbasketService.triggerWorkBasketSaved();
			this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS,
				`Workbasket ${this.workbasketIdSelected} was removed successfully`))
			this.router.navigate(['/workbaskets']);
		}, error => {
			this.requestInProgressService.setRequestInProgress(false);
			this.errorModalService.triggerError(new ErrorModel(
				`There was an error deleting workbasket ${this.workbasketIdSelected}`, error.error.message))
		});
	}

	copyWorkbasket() {
		this.router.navigate([{ outlets: { detail: ['copy-workbasket'] } }], { relativeTo: this.route });
	}

	private findWorkbasketSelectedObject() {
		return this.workbaskets.find(element => element.workbasketId === this.workbasketIdSelected);
	}
}
