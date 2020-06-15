import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Filter } from 'app/shared/models/filter';
import { Sorting } from 'app/shared/models/sorting';
import { Orientation } from 'app/shared/models/orientation';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { TaskanaQueryParameters } from 'app/shared/util/query-parameters';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Select, Store } from '@ngxs/store';
import { GetWorkbaskets,
  GetWorkbasketsSummary,
  SelectWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { Workbasket } from '../../../shared/models/workbasket';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';

@Component({
  selector: 'taskana-administration-workbasket-list',
  templateUrl: './workbasket-list.component.html',
  styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit, OnDestroy {
  selectedId = '';
  workbasketsResource: WorkbasketSummaryRepresentation;
  workbaskets: Array<WorkbasketSummary> = [];
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

  @Select(WorkbasketSelectors.workbaskets) workbaskets$: Observable<Workbasket[]>;

  private workBasketSummarySubscription: Subscription;
  private workbasketServiceSubscription: Subscription;
  private workbasketServiceSavedSubscription: Subscription;
  private orientationSubscription: Subscription;
  private importingExportingSubscription: Subscription;

  constructor(
    private store: Store,
    private workbasketService: WorkbasketService,
    private router: Router,
    private route: ActivatedRoute,
    private orientationService: OrientationService,
    private importExportService: ImportExportService
  ) {
  }

  ngOnInit() {
    this.requestInProgress = true;
    this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe(workbasketIdSelected => {
      // TODO should be done in a different way.
      setTimeout(() => {
        this.selectedId = workbasketIdSelected;
      }, 0);
    });

    TaskanaQueryParameters.page = this.pageSelected;
    TaskanaQueryParameters.pageSize = this.pageSize;

    this.workbasketServiceSavedSubscription = this.workbasketService.workbasketSavedTriggered().subscribe(value => {
      this.performRequest();
    });
    this.orientationSubscription = this.orientationService.getOrientation().subscribe((orientation: Orientation) => {
      this.refreshWorkbasketList();
    });
    this.importingExportingSubscription = this.importExportService.getImportingFinished().subscribe((value: Boolean) => {
      this.refreshWorkbasketList();
    });
  }

  selectWorkbasket(id: string) {
    this.store.dispatch(new SelectWorkbasket(id));
    this.selectedId = id;
    this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
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
    this.requestInProgress = true;
    this.workbaskets = [];
    this.workbasketServiceSubscription = this.workbasketService.getWorkBasketsSummary(
      true, this.sort.sortBy, this.sort.sortDirection, '',
      this.filterBy.filterParams.name, this.filterBy.filterParams.description, '', this.filterBy.filterParams.owner,
      this.filterBy.filterParams.type, '', this.filterBy.filterParams.key, ''
    )
      .subscribe(resultList => {
        this.workbasketsResource = resultList;
        this.workbaskets = resultList.workbaskets;
        this.requestInProgress = false;
      });
  }

  ngOnDestroy() {
    if (this.workBasketSummarySubscription) {
      this.workBasketSummarySubscription.unsubscribe();
    }
    if (this.workbasketServiceSubscription) {
      this.workbasketServiceSubscription.unsubscribe();
    }
    if (this.workbasketServiceSavedSubscription) {
      this.workbasketServiceSavedSubscription.unsubscribe();
    }
    if (this.orientationSubscription) {
      this.orientationSubscription.unsubscribe();
    }

    if (this.importingExportingSubscription) {
      this.importingExportingSubscription.unsubscribe();
    }
  }
}
