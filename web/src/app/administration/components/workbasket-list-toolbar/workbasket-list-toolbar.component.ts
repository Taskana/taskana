import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Sorting, WORKBASKET_SORT_PARAMETER_NAMING, WorkbasketQuerySortParameter } from 'app/shared/models/sorting';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { expandDown } from 'app/shared/animations/expand.animation';
import { Select, Store } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ACTION } from '../../../shared/models/action';
import { CreateWorkbasket } from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { WorkbasketService } from '../../../shared/services/workbasket/workbasket.service';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-parameters';
import { Pair } from '../../../shared/models/pair';

@Component({
  selector: 'taskana-administration-workbasket-list-toolbar',
  animations: [expandDown],
  templateUrl: './workbasket-list-toolbar.component.html',
  styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {
  @Input() workbasketListExpanded: boolean = true;
  @Input() workbaskets: Array<WorkbasketSummary>;
  @Input() workbasketDefaultSortBy: WorkbasketQuerySortParameter;
  @Output() performSorting = new EventEmitter<Sorting<WorkbasketQuerySortParameter>>();
  @Output() performFilter = new EventEmitter<WorkbasketQueryFilterParameter>();

  selectionToImport = TaskanaType.WORKBASKETS;
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

  filtering({ left: component, right: filter }: Pair<string, WorkbasketQueryFilterParameter>) {
    if (component === 'workbasket-list') {
      this.performFilter.emit(filter);
    }
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
