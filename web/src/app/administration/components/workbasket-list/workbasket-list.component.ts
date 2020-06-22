import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, pipe, Subject, Subscription } from 'rxjs';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { Sorting } from 'app/shared/models/sorting';
import { Orientation } from 'app/shared/models/orientation';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Actions, ofActionCompleted, ofActionDispatched, Select, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import { Location } from '@angular/common';
import { GetWorkbasketsSummary,
  SelectWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';

@Component({
  selector: 'taskana-workbasket-list',
  templateUrl: './workbasket-list.component.html',
  styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit, OnDestroy {
  selectedId = '';
  requestInProgress = false;
  pageSelected = 1;
  pageSize = 9;
  type = 'workbaskets';
  cards: number = this.pageSize;
  workbasketDefaultSortBy: string = 'name';
  sort: Sorting = new Sorting(this.workbasketDefaultSortBy);
  filterBy: Filter = new Filter({ name: '', owner: '', type: '', description: '', key: '' });

  @ViewChild('wbToolbar', { static: true })
  private toolbarElement: ElementRef;

  @Select(WorkbasketSelectors.workbasketsSummary)
  workbasketsSummary$: Observable<WorkbasketSummary[]>;

  @Select(WorkbasketSelectors.workbasketsSummaryRepresentation)
  workbasketsSummaryRepresentation$: Observable<WorkbasketSummaryRepresentation>;

  destroy$ = new Subject<void>();

  constructor(
    private store: Store,
    private workbasketService: WorkbasketService,
    private router: Router,
    private route: ActivatedRoute,
    private orientationService: OrientationService,
    private importExportService: ImportExportService,
    private ngxsActions$: Actions,
    private location: Location
  ) {
    this.ngxsActions$.pipe(ofActionDispatched(GetWorkbasketsSummary),
      takeUntil(this.destroy$))
      .subscribe(() => {
        this.requestInProgress = true;
      });
    this.ngxsActions$.pipe(ofActionCompleted(GetWorkbasketsSummary),
      takeUntil(this.destroy$))
      .subscribe(() => {
        this.requestInProgress = false;
      });
  }

  ngOnInit() {
    this.requestInProgress = true;
    this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
      // TODO should be done in a different way.
      setTimeout(() => {
        this.selectedId = workbasketIdSelected;
      }, 0);
    });

    TaskanaQueryParameters.page = this.pageSelected;
    TaskanaQueryParameters.pageSize = this.pageSize;

    this.workbasketService.workbasketSavedTriggered()
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        this.performRequest();
      });
    this.orientationService.getOrientation()
      .pipe(takeUntil(this.destroy$))
      .subscribe((orientation: Orientation) => {
        this.refreshWorkbasketList();
      });
    this.importExportService.getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value: Boolean) => {
        this.refreshWorkbasketList();
      });
  }

  selectWorkbasket(id: string) {
    this.store.dispatch(new SelectWorkbasket(id));
    this.selectedId = id;
    this.location.go(this.location.path().replace(/(workbaskets).*/g, `workbaskets/(detail:${id})`));

    // this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
  }

  performSorting(sort: Sorting) {
    this.sort = sort;
    this.performRequest();
  }

  performFilter(filterBy: Filter) {
    this.filterBy = filterBy;
    this.performRequest();
  }

  changePage(page) {
    TaskanaQueryParameters.page = page;
    this.performRequest();
  }

  refreshWorkbasketList() {
    this.cards = this.orientationService.calculateNumberItemsList(
      window.innerHeight, 72, 170 + this.toolbarElement.nativeElement.offsetHeight, false
    );
    this.performRequest();
  }

  private performRequest(): void {
    this.store.dispatch(new GetWorkbasketsSummary(true, this.sort.sortBy, this.sort.sortDirection, '',
      this.filterBy.filterParams.name, this.filterBy.filterParams.description, '', this.filterBy.filterParams.owner,
      this.filterBy.filterParams.type, '', this.filterBy.filterParams.key, ''));
    TaskanaQueryParameters.pageSize = this.cards;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
