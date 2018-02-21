import { Component, OnInit, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../../model/workbasketSummary';
import { WorkbasketService, Direction } from '../../services/workbasketservice.service'
import { Subscription } from 'rxjs/Subscription';
import { FilterModel } from '../../shared/filter/filter.component'
import { filter } from 'rxjs/operator/filter';

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

	sortBy: string = 'key';
	sortDirection: Direction = Direction.ASC;
	sortingFields: Map<string, string> = new Map([['name', 'Name'], ['key', 'Id'], ['description', 'Description'], ['owner', 'Owner'], ['type', 'Type']]);
	filterBy: FilterModel = new FilterModel();

	private workBasketSummarySubscription: Subscription;
	private workbasketServiceSubscription: Subscription;

	constructor(private workbasketService: WorkbasketService) { }

	ngOnInit() {
		this.requestInProgress = true;
		this.workBasketSummarySubscription = this.workbasketService.getWorkBasketsSummary().subscribe(resultList => {
			this.workbaskets = resultList;
			this.requestInProgress = false;
		});

		this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
			this.selectedId = workbasketIdSelected;
		});
	}

	selectWorkbasket(id: string) {
		this.selectedId = id;
	}

	changeOrder(sortDirection: string) {
		this.sortDirection = (sortDirection === Direction.ASC) ? Direction.ASC : Direction.DESC;
		this.performRequest();
	}

	changeSortBy(sortBy: string) {
		this.sortBy = sortBy;
		this.performRequest();
	}

	performFilter(filterBy: FilterModel) {
		this.filterBy = filterBy;
		this.performRequest();
	}

	onDelete(workbasket: WorkbasketSummary) {
		this.workbasketService.deleteWorkbasket(workbasket.workbasketId).subscribe(result => {
			var index = this.workbaskets.indexOf(workbasket);
			if (index > -1) {
				this.workbaskets.splice(index, 1);
			}
		});
	}

	onAdd() {
		this.workbasketService.createWorkbasket(this.newWorkbasket).subscribe(result => {
			this.workbaskets.push(result);
			this.onClear();
		});
	}

	onClear() {
		this.newWorkbasket.workbasketId = "";
		this.newWorkbasket.name = "";
		this.newWorkbasket.description = "";
		this.newWorkbasket.owner = "";
	}

	getEmptyObject() {
		return new WorkbasketSummary("", "", "", "", "", "", "", "", "", "", "", "");
	}

	private performRequest(): void {
		this.requestInProgress = true;
		this.workbaskets = undefined;
		this.workbasketServiceSubscription.add(this.workbasketService.getWorkBasketsSummary(this.sortBy, this.sortDirection, undefined,
			this.filterBy.name, this.filterBy.description, undefined, this.filterBy.owner,
			this.filterBy.type, undefined, this.filterBy.key).subscribe(resultList => {
				this.workbaskets = resultList;
				this.requestInProgress = false;
			}));
	}

	private ngOnDestroy() {
		this.workBasketSummarySubscription.unsubscribe();
		this.workbasketServiceSubscription.unsubscribe();
	}

}
