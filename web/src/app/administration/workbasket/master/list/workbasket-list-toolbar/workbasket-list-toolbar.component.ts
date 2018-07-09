import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { SortingModel } from 'app/models/sorting';
import { FilterModel } from 'app/models/filter';
import { Subscription } from 'rxjs/Subscription';
import { WorkbasketSummary } from 'app/models/workbasket-summary';

import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { WorkbasketService } from 'app/services/workbasket/workbasket.service';
import { AlertService } from 'app/services/alert/alert.service';
import { ImportType } from 'app/models/import-type';
import { expandDown } from 'app/shared/animations/expand.animation';

@Component({
	selector: 'taskana-workbasket-list-toolbar',
	animations: [expandDown],
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
