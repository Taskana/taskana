import { Component, OnInit, Input, Output } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketService } from '../../../services/workbasketservice.service';

@Component({
  selector: 'workbasket-information',
  templateUrl: './workbasket-information.component.html',
  styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnInit {

  @Input()
  workbasket: Workbasket;

  constructor(private service: WorkbasketService) { }

  ngOnInit() {
  }

  selectType(type: number){
    this.workbasket.type = type === 0 ? 'PERSONAL': 'MULTIPLE';
  }
}
