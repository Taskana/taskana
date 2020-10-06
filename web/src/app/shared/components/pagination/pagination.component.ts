import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChild, OnInit } from '@angular/core';
import { Page } from 'app/shared/models/page';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'taskana-shared-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, OnChanges {
  @Input()
  page: Page;

  @Input()
  type: String;

  @Input()
  numberOfItems: number;

  @Output()
  changePage = new EventEmitter<number>();

  @ViewChild(MatPaginator, { static: true })
  paginator: MatPaginator;

  hasItems = true;
  pageSelected = 1;

  ngOnInit() {
    this.paginator._intl.itemsPerPageLabel = 'Per page';
    this.paginator._intl.getRangeLabel = (page: number, pageSize: number, length: number) => {
      page += 1;
      const start = length / pageSize >= page ? (page + 1) * pageSize : length;
      const end = length;
      return `${start} of ${end} workbaskets`;
    };
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes.page && changes.page.currentValue) {
      this.pageSelected = changes.page.currentValue.number;
    }
    this.hasItems = this.numberOfItems > 0;
  }

  changeToPage(event) {
    let currentPageIndex = event.pageIndex;
    if (currentPageIndex > event.previousPageIndex) {
      this.pageSelected += 1;
    } else {
      this.pageSelected -= 1;
    }
    this.changePage.emit(currentPageIndex + 1);
  }
}
