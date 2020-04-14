import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SortingModel } from 'app/models/sorting';
import { FilterModel } from 'app/models/filter';
import { Subscription } from 'rxjs';
import { WorkbasketSummary } from 'app/models/workbasket-summary';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { TaskanaType } from 'app/models/taskana-type';
import { expandDown } from 'app/shared/animations/expand.animation';
import { ErrorsService } from '../../../services/errors/errors.service';
import { ERROR_TYPES } from '../../../models/errors';

@Component({
  selector: 'taskana-workbasket-list-toolbar',
  animations: [expandDown],
  templateUrl: './workbasket-list-toolbar.component.html',
  styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {
  @Input() workbaskets: Array<WorkbasketSummary>;
  @Input() workbasketDefaultSortBy: string;
  @Output() performSorting = new EventEmitter<SortingModel>();
  @Output() performFilter = new EventEmitter<FilterModel>();
  workbasketServiceSubscription: Subscription;
  selectionToImport = TaskanaType.WORKBASKETS;
  sortingFields = new Map([['name', 'Name'], ['key', 'Key'], ['description', 'Description'], ['owner', 'Owner'], ['type', 'Type']]);
  filteringTypes = new Map([['ALL', 'All'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'],
    ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']]);

  filterParams = { name: '', key: '', type: '', description: '', owner: '' };
  toolbarState = false;
  filterType = TaskanaType.WORKBASKETS;

  constructor(
    private workbasketService: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
    private errors: ErrorsService
  ) {
  }

  ngOnInit() {
  }

  sorting(sort: SortingModel) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: FilterModel) {
    this.performFilter.emit(filterBy);
  }

  addWorkbasket() {
    this.workbasketService.selectWorkBasket();
    this.router.navigate([{ outlets: { detail: ['new-workbasket'] } }], { relativeTo: this.route });
  }
}
