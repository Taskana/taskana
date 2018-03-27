import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';

@Component({
	selector: 'taskana-pagination',
	templateUrl: './pagination.component.html',
	styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit {

	@Input()
	workbasketsResource: WorkbasketSummaryResource;
	@Output()
	workbasketsResourceChange = new EventEmitter<WorkbasketSummaryResource>();
	@Output() changePage = new EventEmitter<number>();
	previousPageSelected = 1;
	pageSelected = 1;
	maxPagesAvailable = 8;

	constructor() { }

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
			this.pageSelected = page;
		}
	}
}
