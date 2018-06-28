import { Component, OnInit, Input, Output, EventEmitter, AfterViewChecked } from '@angular/core';
import { trigger, state, style, transition, animate, keyframes } from '@angular/animations';
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
import { AlertService } from 'app/services/alert/alert.service';
import { ImportType } from 'app/models/import-type';

@Component({
	selector: 'taskana-workbasket-list-toolbar',
	animations: [
		trigger('toggle', [
			transition('void => *', animate('300ms ease-in', keyframes([
				style({ height: '0px' }),
				style({ height: '50px' }),
				style({ height: '*' })]))),
			transition('* => void', animate('300ms ease-out', keyframes([
				style({ height: '*' }),
				style({ height: '50px' }),
				style({ height: '0px' })])))
		]
		)],
	templateUrl: './workbasket-list-toolbar.component.html',
	styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {


	@Input() workbaskets: Array<WorkbasketSummary>;
	@Output() performSorting = new EventEmitter<SortingModel>();
	@Output() performFilter = new EventEmitter<FilterModel>();
	@Output() importSucessful = new EventEmitter();
	workbasketServiceSubscription: Subscription;
	selectionToImport = ImportType.WORKBASKETS;
	sortingFields = new Map([['name', 'Name'], ['key', 'Key'], ['description', 'Description'], ['owner', 'Owner'], ['type', 'Type']]);
	toolbarState = false;

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
		this.workbasketService.selectWorkBasket(undefined);
		this.router.navigate([{ outlets: { detail: ['new-workbasket'] } }], { relativeTo: this.route });
	}

	importEvent() {
		this.importSucessful.emit();
	}
}
