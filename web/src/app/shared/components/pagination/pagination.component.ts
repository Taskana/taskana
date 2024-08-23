import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { Page } from 'app/shared/models/page';
import { MatPaginator } from '@angular/material/paginator';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'kadai-shared-pagination',
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

  @Input()
  expanded: boolean = true;

  @Input()
  resetPaging: Observable<null>;

  @Output()
  changePage = new EventEmitter<number>();

  @ViewChild(MatPaginator, { static: true })
  paginator: MatPaginator;

  @ViewChild('pagination') paginationWrapper: ElementRef;

  destroy$ = new Subject<void>();

  hasItems = true;
  pageSelected = 1;
  pageNumbers: number[];
  filteredPages: string[] = [];

  ngOnInit() {
    this.changeLabel();
    if (this.resetPaging) this.resetPaging.pipe(takeUntil(this.destroy$)).subscribe(() => this.goToPage(1));
  }

  ngOnChanges(changes: SimpleChanges): void {
    const rangeLabel = this.paginationWrapper?.nativeElement?.querySelector('.mat-paginator-range-label');
    const container = this.paginationWrapper?.nativeElement?.querySelector('.mat-paginator-container');
    if (rangeLabel && container) {
      if (!this.expanded) {
        container.style.justifyContent = 'center';
        rangeLabel.style.display = 'none';
      } else {
        container.style.justifyContent = 'flex-end';
        rangeLabel.style.display = 'block';
      }
    }

    if (changes.page && changes.page.currentValue) {
      this.pageSelected = changes.page.currentValue.number;
    }
    this.hasItems = this.numberOfItems > 0;
    if (changes.page) {
      this.updateGoto();
    }
  }

  changeLabel() {
    // Custom label: EG. "1-7 of 21 workbaskets"
    // return `${start} - ${end} of ${length} workbaskets`;

    this.paginator._intl.itemsPerPageLabel = 'Per page';
    this.paginator._intl.getRangeLabel = (page: number, pageSize: number, length: number) => {
      page += 1;
      const start = pageSize * (page - 1) + 1;
      const end = pageSize * page < length ? pageSize * page : length;
      if (length === 0) {
        return 'loading...';
      } else {
        return `${start} - ${end} of ${length}`;
      }
    };
    this.pageSelected = 1;
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

  updateGoto() {
    this.pageNumbers = [];
    for (let i = 1; i <= this.page?.totalPages; i++) {
      this.pageNumbers.push(i);
    }
  }

  goToPage(page: number) {
    this.paginator.pageIndex = page - 1;
    this.pageSelected = page;
    this.changePage.emit(page);
  }

  filter(filterValue) {
    const pageNumbers = this.pageNumbers.map(String);
    this.filteredPages = pageNumbers.filter((value) => value.includes(filterValue.toString()));
    if (this.filteredPages.length === 0) {
      this.filteredPages = pageNumbers;
    }
  }

  onSelectText() {
    const input = document.getElementById('inputTypeAhead') as HTMLInputElement;
    input.focus();
    input.select();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
