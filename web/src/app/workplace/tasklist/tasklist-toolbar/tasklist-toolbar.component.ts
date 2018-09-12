import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {Workbasket} from 'app/models/workbasket';
import {TaskService} from 'app/workplace/services/task.service';
import {WorkbasketService} from 'app/services/workbasket/workbasket.service';
import {SortingModel} from 'app/models/sorting';
import {FilterModel} from 'app/models/filter';
import {TaskanaType} from 'app/models/taskana-type';
import {expandDown} from 'app/shared/animations/expand.animation';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkplaceService} from 'app/workplace/services/workplace.service';

@Component({
  selector: 'taskana-tasklist-toolbar',
  animations: [expandDown],
  templateUrl: './tasklist-toolbar.component.html',
  styleUrls: ['./tasklist-toolbar.component.scss']
})
export class TaskListToolbarComponent implements OnInit {

  @Output() performSorting = new EventEmitter<SortingModel>();
  @Output() performFilter = new EventEmitter<FilterModel>();


  sortingFields = new Map([['name', 'Name'], ['priority', 'Priority'], ['due', 'Due'], ['planned', 'Planned']]);
  filterParams = {name: '', key: '', owner: '', priority: '', state: ''};
  tasks: Task[] = [];

  workbasketNames: string[] = [];
  resultName = '';
  resultId = '';
  workbaskets: Workbasket[];
  currentBasket: Workbasket;
  workbasketSelected = false;
  toolbarState = false;
  filterType = TaskanaType.TASKS;

  constructor(private taskService: TaskService,
              private workbasketService: WorkbasketService,
              private workplaceService: WorkplaceService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.workbasketService.getAllWorkBaskets().subscribe(workbaskets => {
      this.workbaskets = workbaskets._embedded ? workbaskets._embedded.workbaskets : [];
      this.workbaskets.forEach(workbasket => {
        this.workbasketNames.push(workbasket.name);
      });
    });
    this.taskService.getSelectedTask().subscribe(t => {
      if (!this.resultName) {
        this.resultName = t.workbasketSummaryResource.name;
        this.resultId = t.workbasketSummaryResource.workbasketId;
        this.currentBasket = t.workbasketSummaryResource;
        this.workplaceService.selectWorkbasket(this.currentBasket);
        this.workbasketSelected = true;
      }
    })
  }

  searchBasket() {
    this.toolbarState = false;
    if (this.workbaskets) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.resultName) {
          this.resultId = workbasket.workbasketId;
          this.currentBasket = workbasket;
          this.workplaceService.selectWorkbasket(this.currentBasket);
        }
      });

      if (!this.resultId) {
        this.currentBasket = undefined;
        this.workplaceService.selectWorkbasket(undefined);
      }
    }
    this.resultId = '';
    this.router.navigate(['']);
  }

  sorting(sort: SortingModel) {
    this.performSorting.emit(sort);
  }

  filtering(filterBy: FilterModel) {
    this.performFilter.emit(filterBy);
  }

  createTask() {
    this.taskService.selectTask(undefined);
    this.router.navigate([{outlets: {detail: 'taskdetail/new-task'}}], {relativeTo: this.route});
  }
}
