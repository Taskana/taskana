import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Sorting, WORKBASKET_SORT_PARAMETER_NAMING, WorkbasketQuerySortParameter } from 'app/shared/models/sorting';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { KadaiType } from 'app/shared/models/kadai-type';
import { expandDown } from 'app/shared/animations/expand.animation';
import { Select, Store } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ACTION } from '../../../shared/models/action';
import { CreateWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';

@Component({
  selector: 'kadai-administration-workbasket-list-toolbar',
  animations: [expandDown],
  templateUrl: './workbasket-list-toolbar.component.html',
  styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {
  @Input() workbasketListExpanded: boolean = true;
  @Input() workbaskets: WorkbasketSummary[];
  @Input() workbasketDefaultSortBy: WorkbasketQuerySortParameter;
  @Output() performSorting = new EventEmitter<Sorting<WorkbasketQuerySortParameter>>();

  selectionToImport = KadaiType.WORKBASKETS;
  sortingFields: Map<WorkbasketQuerySortParameter, string> = WORKBASKET_SORT_PARAMETER_NAMING;

  isExpanded = false;
  showFilter = false;

  @Select(WorkbasketSelectors.workbasketActiveAction)
  workbasketActiveAction$: Observable<ACTION>;

  destroy$ = new Subject<void>();
  action: ACTION;

  constructor(private store: Store, private workbasketService: WorkbasketService) {}

  ngOnInit() {
    this.workbasketActiveAction$.pipe(takeUntil(this.destroy$)).subscribe((action) => {
      this.action = action;
    });
  }

  sorting(sort: Sorting<WorkbasketQuerySortParameter>) {
    this.performSorting.emit(sort);
  }

  addWorkbasket() {
    if (this.action !== ACTION.CREATE) {
      this.store.dispatch(new CreateWorkbasket());
    }
  }

  onClickFilter() {
    this.isExpanded = !this.isExpanded;
    this.workbasketService.expandWorkbasketActionToolbar(this.isExpanded);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
