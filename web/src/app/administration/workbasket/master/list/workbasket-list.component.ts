import { Component, OnInit, EventEmitter, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { FilterModel } from 'app/models/filter'
import { SortingModel } from 'app/models/sorting';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service'

@Component({
	selector: 'taskana-workbasket-list',
	templateUrl: './workbasket-list.component.html',
	styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit, OnDestroy {

	selectedId = '';
	workbaskets: Array<WorkbasketSummary> = [];
	requestInProgress = false;

	sort: SortingModel = new SortingModel();
	filterBy: FilterModel = new FilterModel();

	private workBasketSummarySubscription: Subscription;
	private workbasketServiceSubscription: Subscription;
	private workbasketServiceSavedSubscription: Subscription;

	constructor(
		private workbasketService: WorkbasketService,
		private router: Router,
		private route: ActivatedRoute,
		private cdRef: ChangeDetectorRef) { }

	ngOnInit() {
		this.requestInProgress = true;
		this.workBasketSummarySubscription = this.workbasketService.getWorkBasketsSummary().subscribe(resultList => {
			this.workbaskets = resultList._embedded ? resultList._embedded.workbaskets : [];
			this.requestInProgress = false;
		});

		this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
			this.selectedId = workbasketIdSelected;
			this.cdRef.detectChanges();
		});

		this.workbasketServiceSavedSubscription = this.workbasketService.workbasketSavedTriggered().subscribe(value => {
			this.performRequest();
		});
	}

	selectWorkbasket(id: string) {
		this.selectedId = id;
		if (!this.selectedId) {
			this.router.navigate(['/workbaskets']);
			return
		}
		this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
	}

	performSorting(sort: SortingModel) {
		this.sort = sort;
		this.performRequest();
	}

	performFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performRequest();
	}

	private performRequest(): void {
		this.requestInProgress = true;
		this.workbaskets = [];
		this.workbasketServiceSubscription.add(this.workbasketService.getWorkBasketsSummary(true, this.sort.sortBy,
			this.sort.sortDirection, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key).subscribe(resultList => {
				this.workbaskets = resultList._embedded ? resultList._embedded.workbaskets : [];
				this.requestInProgress = false;
				this.unSelectWorkbasket();
			}));
	}


	private unSelectWorkbasket(): void {
		if (!this.workbaskets.find(wb => wb.workbasketId === this.selectedId)) {
			this.selectWorkbasket(undefined);
		}
	}

	ngOnDestroy() {
		this.workBasketSummarySubscription.unsubscribe();
		this.workbasketServiceSubscription.unsubscribe();
		this.workbasketServiceSavedSubscription.unsubscribe();

	}

}
