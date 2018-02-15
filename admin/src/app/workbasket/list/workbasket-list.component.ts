import { Component, OnInit, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../../model/workbasketSummary';
import { WorkbasketService } from '../../services/workbasketservice.service'
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
  requestInProgress: boolean =  false;

  private workBasketSummarySubscription: Subscription;
  private workbasketServiceSubscription: Subscription;

  constructor(private workbasketService: WorkbasketService) { }

  ngOnInit() {
    this.requestInProgress = true;
    this.workBasketSummarySubscription = this.workbasketService.getWorkBasketsSummary().subscribe(resultList => {
      this.workbaskets = resultList;
      this.requestInProgress = false;
    });

    this.workbasketServiceSubscription = this.workbasketService.getSelectedWorkBasket().subscribe( workbasketIdSelected => {
        this.selectedId = workbasketIdSelected;
    });
  }

  selectWorkbasket(id:string){
    this.selectedId = id;
  }

  onDelete(workbasket: WorkbasketSummary) {
    this.workbasketService.deleteWorkbasket(workbasket.id).subscribe(result => {
      var index = this.workbaskets.indexOf(workbasket);
      if (index > -1) {
        this.workbaskets.splice(index, 1);
      }
    });
  }

  onAdd() {
    this.workbasketService.createWorkbasket(this.newWorkbasket).subscribe(result => {
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
    return new WorkbasketSummary("", "", "", "", "", "", "", "", "", "", "", "");
  }

  ngOnDestroy(){
    this.workBasketSummarySubscription.unsubscribe();
    this.workbasketServiceSubscription.unsubscribe();
  }

}
