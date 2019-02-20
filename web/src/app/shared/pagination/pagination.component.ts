import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { Page } from 'app/models/page';

@Component({
  selector: 'taskana-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnChanges {
  @Input()
  page: Page;
  @Input()
  type: String;
  @Output()
  workbasketsResourceChange = new EventEmitter<Page>();
  @Output()
  changePage = new EventEmitter<number>();
  @Input()
  numberOfItems: number;
  previousPageSelected = 1;
  pageSelected = 1;
  maxPagesAvailable = 8;

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.page && changes.page.currentValue) {
      this.pageSelected = changes.page.currentValue.number;
    }
  }

  changeToPage(page) {
    if (page < 1) {
      page = this.pageSelected = 1;
    }
    if (page > this.page.totalPages) {
      page = this.page.totalPages;
    }
    if (this.previousPageSelected !== page) {
      this.changePage.emit(page);
      this.previousPageSelected = page;
      this.page.number = page;
      this.pageSelected = page;
    }
  }

  getPagesTextToShow(): string {
    if (!this.page) {
      return '';
    }
    const text = this.numberOfItems + '';
    return `${text} of ${this.page.totalElements} ${this.type}`;
  }
}
