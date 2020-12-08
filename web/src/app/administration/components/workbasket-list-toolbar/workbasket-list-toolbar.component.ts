import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Sorting } from 'app/shared/models/sorting';
import { Filter } from 'app/shared/models/filter';
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

@Component({
  selector: 'taskana-administration-workbasket-list-toolbar',
  animations: [expandDown],
  templateUrl: './workbasket-list-toolbar.component.html',
  styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {
  @Input() workbaskets: Array<WorkbasketSummary>;
  @Input() workbasketDefaultSortBy: string;
  @Output() performSorting = new EventEmitter<Sorting>();
  @Output() performFilter = new EventEmitter<Filter>();

  selectionToImport = TaskanaType.WORKBASKETS;
  sortingFields = new Map([
    ['name', 'Name'],
    ['key', 'Key'],
    ['description', 'Description'],
    ['owner', 'Owner'],
    ['type', 'Type']
  ]);
  filteringTypes = new Map([
    ['ALL', 'All'],
    ['PERSONAL', 'Personal'],
    ['GROUP', 'Group'],
    ['CLEARANCE', 'Clearance'],
    ['TOPIC', 'Topic']
  ]);

  filterParams = { name: '', key: '', type: '', description: '', owner: '' };
  filterType = TaskanaType.WORKBASKETS;
  showFilter = false;
  component = '';

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

  sorting(sort: Sorting) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: Filter) {
    if (this.component === 'workbasket-list') {
      this.performFilter.emit(filterBy);
    }
  }

  setComponent(component: string) {
    this.component = component;
  }

  addWorkbasket() {
    if (this.action !== ACTION.CREATE) {
      this.store.dispatch(new CreateWorkbasket());
    }
  }

  onClickFilter() {
    this.showFilter = !this.showFilter;
    this.workbasketService.expandWorkbasketActionToolbar(this.showFilter);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
