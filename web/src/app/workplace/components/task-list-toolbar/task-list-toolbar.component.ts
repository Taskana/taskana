import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/shared/models/workbasket';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Sorting } from 'app/shared/models/sorting';
import { Filter } from 'app/shared/models/filter';
import { TaskanaType } from 'app/shared/models/taskana-type';
import { expandDown } from 'app/shared/animations/expand.animation';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { ObjectReference } from 'app/workplace/models/object-reference';

export enum Search {
  byWorkbasket = 'workbasket',
  byTypeAndValue = 'type-and-value'
}

@Component({
  selector: 'taskana-task-list-toolbar',
  animations: [expandDown],
  templateUrl: './task-list-toolbar.component.html',
  styleUrls: ['./task-list-toolbar.component.scss']
})
export class TaskListToolbarComponent implements OnInit {
  @Input() taskDefaultSortBy: string;
  @Output() performSorting = new EventEmitter<Sorting>();
  @Output() performFilter = new EventEmitter<Filter>();
  @Output() selectSearchType = new EventEmitter();

  sortingFields = new Map([
    ['name', 'Name'],
    ['priority', 'Priority'],
    ['due', 'Due'],
    ['planned', 'Planned']
  ]);
  filterParams = { name: '', key: '', owner: '', priority: '', state: '' };
  tasks: Task[] = [];

  workbasketNames: string[] = [];
  resultName = '';
  resultId = '';
  workbaskets: Workbasket[];
  currentBasket: Workbasket;
  workbasketSelected = false;
  toolbarState = false;
  filterType = TaskanaType.TASKS;
  searched = false;

  search = Search;
  searchSelected: Search = Search.byWorkbasket;
  resultType = '';
  resultValue = '';

  constructor(
    private taskService: TaskService,
    private workbasketService: WorkbasketService,
    private workplaceService: WorkplaceService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.workbasketService.getAllWorkBaskets().subscribe((workbaskets) => {
      this.workbaskets = workbaskets.workbaskets;
      this.workbaskets.forEach((workbasket) => {
        this.workbasketNames.push(workbasket.name);
      });
    });
    this.taskService.getSelectedTask().subscribe((t) => {
      if (!this.resultName) {
        this.resultName = t.workbasketSummary.name;
        this.resultId = t.workbasketSummary.workbasketId;
        this.currentBasket = t.workbasketSummary;
        this.workplaceService.selectWorkbasket(this.currentBasket);
        this.workbasketSelected = true;
      }
    });

    if (this.route.snapshot.queryParams.search === this.search.byTypeAndValue) {
      this.searchSelected = this.search.byTypeAndValue;
    }
    if (this.router.url.includes('taskdetail')) {
      this.searched = true;
    }
  }

  searchBasket() {
    this.toolbarState = false;
    this.workbasketSelected = true;
    if (this.searchSelected === this.search.byTypeAndValue) {
      const objectReference = new ObjectReference();
      objectReference.type = this.resultType;
      objectReference.value = this.resultValue;
      this.workplaceService.selectObjectReference(objectReference);
      this.searched = true;
    } else {
      if (this.workbaskets) {
        this.workbaskets.forEach((workbasket) => {
          if (workbasket.name === this.resultName) {
            this.resultId = workbasket.workbasketId;
            this.currentBasket = workbasket;
            this.searched = true;
            this.workplaceService.selectWorkbasket(this.currentBasket);
          }
        });

        if (!this.resultId) {
          delete this.currentBasket;
          this.workplaceService.selectWorkbasket();
        }
      }
    }

    this.resultId = '';
    this.router.navigate(['']);
  }

  sorting(sort: Sorting) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: Filter) {
    this.performFilter.emit(filterBy);
  }

  createTask() {
    this.taskService.selectTask();
    this.router.navigate([{ outlets: { detail: 'taskdetail/new-task' } }], { relativeTo: this.route });
  }

  selectSearch(type: Search) {
    this.searched = false;
    delete this.resultId;
    delete this.currentBasket;
    this.selectSearchType.emit(type);
    this.searchSelected = type;

    const navigationExtras: NavigationExtras = {
      queryParams: { search: type }
    };

    this.router.navigate([''], navigationExtras);
    this.searchBasket();
  }
}
