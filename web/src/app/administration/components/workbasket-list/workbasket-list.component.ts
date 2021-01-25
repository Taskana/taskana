import { Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Direction, Sorting, WorkbasketQuerySortParameter } from 'app/shared/models/sorting';
import { Orientation } from 'app/shared/models/orientation';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Actions, ofActionCompleted, ofActionDispatched, Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import {
  DeselectWorkbasket,
  GetWorkbasketsSummary,
  SelectWorkbasket
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { Workbasket } from '../../../shared/models/workbasket';
import { MatSelectionList } from '@angular/material/list';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { QueryPagingParameter } from '../../../shared/models/query-paging-parameter';

@Component({
  selector: 'taskana-administration-workbasket-list',
  templateUrl: './workbasket-list.component.html',
  styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit, OnDestroy {
  selectedId = '';
  type = 'workbaskets';
  workbasketDefaultSortBy: WorkbasketQuerySortParameter = WorkbasketQuerySortParameter.NAME;
  sort: Sorting<WorkbasketQuerySortParameter> = {
    'sort-by': this.workbasketDefaultSortBy,
    order: Direction.ASC
  };
  filterBy: WorkbasketQueryFilterParameter = {};
  pageParameter: QueryPagingParameter = {
    page: 1,
    'page-size': 9
  };
  requestInProgress: boolean;
  requestInProgressLocal = false;
  @Input() expanded: boolean;

  @ViewChild('wbToolbar', { static: true })
  private toolbarElement: ElementRef;

  @Select(WorkbasketSelectors.workbasketsSummary)
  workbasketsSummary$: Observable<WorkbasketSummary[]>;

  @Select(WorkbasketSelectors.workbasketsSummaryRepresentation)
  workbasketsSummaryRepresentation$: Observable<WorkbasketSummaryRepresentation>;

  @Select(WorkbasketSelectors.selectedWorkbasket)
  selectedWorkbasket$: Observable<Workbasket>;

  destroy$ = new Subject<void>();

  @ViewChild('workbasket') workbasketList: MatSelectionList;

  constructor(
    private store: Store,
    private workbasketService: WorkbasketService,
    private orientationService: OrientationService,
    private importExportService: ImportExportService,
    private domainService: DomainService,
    private requestInProgressService: RequestInProgressService,
    private ngxsActions$: Actions
  ) {
    this.ngxsActions$.pipe(ofActionDispatched(GetWorkbasketsSummary), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(true);
      this.requestInProgressLocal = true;
    });
    this.ngxsActions$.pipe(ofActionCompleted(GetWorkbasketsSummary), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
      this.requestInProgressLocal = false;
    });
  }

  ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);

    this.selectedWorkbasket$.pipe(takeUntil(this.destroy$)).subscribe((selectedWorkbasket) => {
      if (typeof selectedWorkbasket !== 'undefined') {
        this.selectedId = selectedWorkbasket.workbasketId;
      } else {
        this.selectedId = undefined;
      }
    });

    this.workbasketService
      .workbasketSavedTriggered()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.performRequest();
      });

    this.orientationService
      .getOrientation()
      .pipe(takeUntil(this.destroy$))
      .subscribe((orientation: Orientation) => {
        this.refreshWorkbasketList();
      });

    this.importExportService
      .getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value: Boolean) => {
        this.refreshWorkbasketList();
      });

    this.domainService
      .getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domain) => {
        this.performRequest();
      });

    this.workbasketService
      .getWorkbasketActionToolbarExpansion()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.requestInProgressService.setRequestInProgress(true);
        setTimeout(() => {
          this.refreshWorkbasketList();
        }, 1);
      });

    this.requestInProgressService
      .getRequestInProgress()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.requestInProgress = value;
      });
  }

  selectWorkbasket(id: string) {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.selectedId === id) {
      this.store
        .dispatch(new DeselectWorkbasket())
        .subscribe(() => this.requestInProgressService.setRequestInProgress(false));
    } else {
      this.store
        .dispatch(new SelectWorkbasket(id))
        .subscribe(() => this.requestInProgressService.setRequestInProgress(false));
    }
  }

  performSorting(sort: Sorting<WorkbasketQuerySortParameter>) {
    this.sort = sort;
    this.performRequest();
  }

  performFilter(filterBy: WorkbasketQueryFilterParameter) {
    this.filterBy = filterBy;
    this.performRequest();
  }

  changePage(page) {
    this.pageParameter.page = page;
    this.performRequest();
  }

  refreshWorkbasketList() {
    this.pageParameter['page-size'] = this.orientationService.calculateNumberItemsList(
      window.innerHeight,
      92,
      200 + this.toolbarElement.nativeElement.offsetHeight,
      false
    );
    this.performRequest();
  }

  performRequest() {
    this.store.dispatch(new GetWorkbasketsSummary(true, this.filterBy, this.sort, this.pageParameter)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
