import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../../model/workbasketSummary';
import { WorkbasketService } from '../../services/workbasketservice.service'
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit {


  workbasket: WorkbasketSummary;
  workbasketClone: WorkbasketSummary;

  workbasketServiceSubscription: Subscription;
  routeSubscription: Subscription;

  constructor(private service: WorkbasketService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.routeSubscription = this.route.params.subscribe(params => {
      this.service.selectWorkBasket(params['id']);
    });

    this.workbasketServiceSubscription = this.service.getSelectedWorkBasket().subscribe( workbasketIdSelected => {
      this.service.getWorkBasket(workbasketIdSelected).subscribe( workbasket => {
        this.workbasket = workbasket;
        this.workbasketClone = { ...this.workbasket };
       });
    });
  }

  ngOnChanges() {
  }

  onSave() {
  }

  ngOnDestroy(){
    this.workbasketServiceSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }
}
