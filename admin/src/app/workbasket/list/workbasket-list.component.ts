import { Component, OnInit, EventEmitter } from '@angular/core';
import { WorkbasketSummaryResource } from '../../model/workbasket-summary-resource';
import { WorkbasketSummary } from '../../model/workbasket-summary';
import { WorkbasketService } from '../../services/workbasket.service'
import { Subscription } from 'rxjs/Subscription';
import { FilterModel } from '../../shared/filter/filter.component'
import { Direction, SortingModel } from '../../shared/sort/sort.component'
import { Router, ActivatedRoute } from '@angular/router';

@Component({
	selector: 'workbasket-list',
	outputs: ['selectedWorkbasket'],
	templateUrl: './workbasket-list.component.html',
	styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit {
	public selectedWorkbasket: EventEmitter<WorkbasketSummary> = new EventEmitter();

	newWorkbasket: WorkbasketSummary;
	selectedId: string = undefined;
	workbaskets: Array<WorkbasketSummary> = [];
	requestInProgress: boolean = false;

	sort: SortingModel = new SortingModel();
	filterBy: FilterModel = new FilterModel();

	private workBasketSummarySubscription: Subscription;
	private workbasketServiceSubscription: Subscription;
	private workbasketServiceSavedSubscription: Subscription;

	constructor(private workbasketService: WorkbasketService, private router: Router, private route: ActivatedRoute) { }

	ngOnInit() {
		this.requestInProgress = true;
		this.workBasketSummarySubscription = this.workbasketService.getWorkBasketsSummary().subscribe(resultList => {
			this.workbaskets = resultList._embedded.workbaskets;
			this.requestInProgress = false;
		});

		this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
			this.selectedId = workbasketIdSelected;
		});

		this.workbasketServiceSavedSubscription = this.workbasketService.workbasketSavedTriggered().subscribe(value => {
			this.performRequest();
		});
	}

	selectWorkbasket(id: string) {
		this.selectedId = id;
		if(!this.selectedId) {
			this.router.navigate(['/workbaskets']);
			return 
		}
		this.router.navigate([{outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
		
	}
	
	performSorting(sort: SortingModel) {
		this.sort = sort;
		this.performRequest();
	
	}

	performFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performRequest();
	}

	onDelete(workbasket: WorkbasketSummary) {

	}

	onAdd() {

	}

	onClear() {
	}

	private performRequest(): void {
		this.requestInProgress = true;
		this.workbaskets = [];
		this.workbasketServiceSubscription.add(this.workbasketService.getWorkBasketsSummary(true, this.sort.sortBy, this.sort.sortDirection, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key).subscribe(resultList => {
				this.workbaskets = resultList._embedded? resultList._embedded.workbaskets:[];
				this.requestInProgress = false;
				this.unSelectWorkbasket();
			}));
	}


	private unSelectWorkbasket() : void{
		if (!this.workbaskets.find( wb => wb.workbasketId === this.selectedId)){
			this.selectWorkbasket(undefined);
		}
	}

	private ngOnDestroy() {
		this.workBasketSummarySubscription.unsubscribe();
		this.workbasketServiceSubscription.unsubscribe();
		this.workbasketServiceSavedSubscription.unsubscribe();
		
	}

}
