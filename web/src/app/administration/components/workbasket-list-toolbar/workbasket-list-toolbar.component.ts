import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Sorting } from 'app/shared/models/sorting';
import { Filter } from 'app/shared/models/filter';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { expandDown } from 'theme/animations/expand.animation';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

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
    private errors: NotificationService
  ) {
  }

  ngOnInit() {
  }

  sorting(sort: Sorting) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: Filter) {
    this.performFilter.emit(filterBy);
  }

  addWorkbasket() {
    this.workbasketService.selectWorkBasket();
    this.router.navigate([{ outlets: { detail: ['new-workbasket'] } }], { relativeTo: this.route });
  }
}
