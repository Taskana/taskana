import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
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
      const start = pageSize * (page - 1) + 1;
      const end = pageSize * page < length ? pageSize * page : length;
      return `${start} - ${end} of ${length} workbaskets`;
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
