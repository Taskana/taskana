import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';

@Component({
	selector: 'taskana-pagination',
	templateUrl: './pagination.component.html',
	styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, OnChanges {

	@Input()
	workbasketsResource: WorkbasketSummaryResource;
	@Output()
	workbasketsResourceChange = new EventEmitter<WorkbasketSummaryResource>();
	@Output() changePage = new EventEmitter<number>();
	previousPageSelected = 1;
	pageSelected = 1;
	maxPagesAvailable = 8;

	constructor() { }

	ngOnChanges(changes: SimpleChanges): void {
		if (changes.workbasketsResource.currentValue && changes.workbasketsResource.currentValue.page) {
			this.pageSelected = changes.workbasketsResource.currentValue.page.number;
		}
	}
	ngOnInit() {
	}

	changeToPage(page) {
		if (page < 1) { page = this.pageSelected = 1; }
		if (page > this.workbasketsResource.page.totalPages) {
			page = this.workbasketsResource.page.totalPages;
		}
		if (this.previousPageSelected !== page) {
			this.changePage.emit(page);
			this.previousPageSelected = page;
		}
	}

	getPagesTextToShow(): string {
		if (!this.workbasketsResource) {
			return '';
		}
		let text = this.workbasketsResource.page.totalElements + '';
		if (this.workbasketsResource.page && this.workbasketsResource.page.totalElements &&
			this.workbasketsResource.page.totalElements >= this.workbasketsResource.page.size) {
			text = this.workbasketsResource.page.size + '';
		}
		return `${text} of ${this.workbasketsResource.page.totalElements} workbaskets`;
	}
}
