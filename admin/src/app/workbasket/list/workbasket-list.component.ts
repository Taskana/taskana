import { Component, OnInit, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../../model/workbasketSummary';
import { WorkbasketService } from '../../services/workbasketservice.service'
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

@Component({
  selector: 'workbasket-list',
  outputs: ['selectedWorkbasket'],
  templateUrl: './workbasket-list.component.html',
  styleUrls: ['./workbasket-list.component.scss']
})
export class WorkbasketListComponent implements OnInit {
  public selectedWorkbasket: EventEmitter<WorkbasketSummary> = new EventEmitter();

  newWorkbasket: WorkbasketSummary;
  selectedId: string = undefined;
  workbaskets : Array<WorkbasketSummary> = [];

  private workBasketSummarySubscription: Subscription;
  private workbasketServiceSubscription: Subscription;

  constructor(private service: WorkbasketService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.workBasketSummarySubscription = this.service.getWorkBasketsSummary().subscribe(resultList => {
      this.workbaskets = resultList;
    });

    this.workbasketServiceSubscription = this.service.getSelectedWorkBasket().subscribe( workbasketIdSelected => {
      this.selectedId = workbasketIdSelected;
    });
  }

  onDelete(workbasket: WorkbasketSummary) {
    this.service.deleteWorkbasket(workbasket.id).subscribe(result => {
      var index = this.workbaskets.indexOf(workbasket);
      if (index > -1) {
        this.workbaskets.splice(index, 1);
      }
    });
  }

  onAdd() {
    this.service.createWorkbasket(this.newWorkbasket).subscribe(result => {
      this.workbaskets.push(result);
      this.onClear();
    });
  }

  onClear() {
    this.newWorkbasket.id = "";
    this.newWorkbasket.name = "";
    this.newWorkbasket.description = "";
    this.newWorkbasket.owner = "";
  }

  getEmptyObject() {
    return new WorkbasketSummary("", "", "", "", "", "", "", "", "", "", "", null);
  }

  ngOnDestroy(){
    this.workBasketSummarySubscription.unsubscribe();
    this.workbasketServiceSubscription.unsubscribe();
  }

}
