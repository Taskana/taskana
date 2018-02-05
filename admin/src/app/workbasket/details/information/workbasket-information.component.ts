import { Component, OnInit, Input, Output } from '@angular/core';
import { WorkbasketSummary } from '../../../model/workbasketSummary';
import { WorkbasketService } from '../../../services/workbasketservice.service';

@Component({
  selector: 'workbasket-information',
  templateUrl: './workbasket-information.component.html',
  styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnInit {

  @Input()
  workbasket: WorkbasketSummary;

  constructor(private service: WorkbasketService) { }

  ngOnInit() {
  }

  ngOnChanges() {
  }

}
