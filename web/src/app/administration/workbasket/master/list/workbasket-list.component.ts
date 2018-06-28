import {
	Component, OnInit, EventEmitter, OnDestroy,
	HostListener, ViewChild, ElementRef, AfterViewChecked, ChangeDetectorRef
} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { FilterModel } from 'app/models/filter'
import { SortingModel } from 'app/models/sorting';
import { Orientation } from 'app/models/orientation';

import { WorkbasketService } from 'app/services/workbasket/workbasket.service'
import { OrientationService } from 'app/services/orientation/orientation.service';

@Component({
	selector: 'taskana-workbasket-list',
	templateUrl: './workbasket-list.component.html',
	styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit, OnDestroy {

	selectedId = '';
	workbasketsResource: WorkbasketSummaryResource;
	workbaskets: Array<WorkbasketSummary> = [];
	requestInProgress = false;

	sort: SortingModel = new SortingModel();
	filterBy: FilterModel = new FilterModel();

	@ViewChild('wbToolbar')
	private toolbarElement: ElementRef;
	private workBasketSummarySubscription: Subscription;
	private workbasketServiceSubscription: Subscription;
	private workbasketServiceSavedSubscription: Subscription;
	private orientationSubscription: Subscription;

	constructor(
		private workbasketService: WorkbasketService,
		private router: Router,
		private route: ActivatedRoute,
		private orientationService: OrientationService,
		private cd: ChangeDetectorRef) { }

	ngOnInit() {
		this.requestInProgress = true;
		this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
			// TODO should be done in a different way.
			setTimeout(() => { this.selectedId = workbasketIdSelected; }, 0);
		});

		this.workbasketServiceSavedSubscription = this.workbasketService.workbasketSavedTriggered().subscribe(value => {
			this.performRequest();
		});
		this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
			this.refreshWorkbasketList();
		})
	}

	selectWorkbasket(id: string) {
		this.selectedId = id;
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

	changePage(page) {
		this.workbasketService.page = page;
		this.performRequest();
	}

	refreshWorkbasketList() {
		const toolbarSize = this.toolbarElement.nativeElement.offsetHeight;
		const cardHeight = 75;
		const unusedHeight = 145
		const totalHeight = window.innerHeight;
		const cards = Math.round((totalHeight - (unusedHeight + toolbarSize)) / cardHeight);
		this.workbasketService.pageSize = cards;
		this.performRequest();
	}

	private performRequest(): void {
		this.requestInProgress = true;
		this.workbaskets = [];
		this.workbasketServiceSubscription = this.workbasketService.getWorkBasketsSummary(
			true, this.sort.sortBy, this.sort.sortDirection, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key, undefined)
			.subscribe(resultList => {
				this.workbasketsResource = resultList;
				this.workbaskets = resultList._embedded ? resultList._embedded.workbaskets : [];
				this.requestInProgress = false;
			});
	}

	ngOnDestroy() {
		if (this.workBasketSummarySubscription) { this.workBasketSummarySubscription.unsubscribe(); }
		if (this.workbasketServiceSubscription) { this.workbasketServiceSubscription.unsubscribe(); }
		if (this.workbasketServiceSavedSubscription) { this.workbasketServiceSavedSubscription.unsubscribe(); }
		if (this.orientationSubscription) { this.orientationSubscription.unsubscribe(); }

	}
}
