import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {animate, keyframes, style, transition, trigger} from '@angular/animations';
import {ActivatedRoute, Router} from '@angular/router';

import {SortingModel} from 'app/models/sorting';
import {FilterModel} from 'app/models/filter';
import {Subscription} from 'rxjs';
import {WorkbasketSummary} from 'app/models/workbasket-summary';

import {ErrorModalService} from 'app/services/errorModal/error-modal.service';
import {RequestInProgressService} from 'app/services/requestInProgress/request-in-progress.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {AlertService} from 'app/services/alert/alert.service';
import {TaskanaType} from 'app/models/taskana-type';

@Component({
  selector: 'taskana-workbasket-list-toolbar',
  animations: [
    trigger('toggle', [
        transition('void => *', animate('300ms ease-in', keyframes([
          style({height: '0px'}),
          style({height: '50px'}),
          style({height: '*'})]))),
        transition('* => void', animate('300ms ease-out', keyframes([
          style({height: '*'}),
          style({height: '50px'}),
          style({height: '0px'})])))
      ]
    )],
  templateUrl: './workbasket-list-toolbar.component.html',
  styleUrls: ['./workbasket-list-toolbar.component.scss']
})
export class WorkbasketListToolbarComponent implements OnInit {


  @Input() workbaskets: Array<WorkbasketSummary>;
  @Output() performSorting = new EventEmitter<SortingModel>();
  @Output() performFilter = new EventEmitter<FilterModel>();
  @Output() importSucessful = new EventEmitter();
  workbasketServiceSubscription: Subscription;
  selectionToImport = TaskanaType.WORKBASKETS;
  sortingFields = new Map([['name', 'Name'], ['key', 'Key'], ['description', 'Description'], ['owner', 'Owner'], ['type', 'Type']]);
  filteringTypes = new Map([['ALL', 'All'], ['PERSONAL', 'Personal'], ['GROUP', 'Group'],
    ['CLEARANCE', 'Clearance'], ['TOPIC', 'Topic']]);
  filterParams = {name: '', key: '', type: '', description: '', owner: ''};
  toolbarState = false;
  filterType = TaskanaType.WORKBASKETS;

  constructor(
    private workbasketService: WorkbasketService,
    private route: ActivatedRoute,
    private router: Router,
    private errorModalService: ErrorModalService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService) {
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
    this.workbasketService.selectWorkBasket(undefined);
    this.router.navigate([{outlets: {detail: ['new-workbasket']}}], {relativeTo: this.route});
  }

  importEvent() {
    this.importSucessful.emit();
  }
}
