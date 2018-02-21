import { Component, OnInit, Input, Output } from '@angular/core';
import { Workbasket } from '../../../model/workbasket';
import { WorkbasketService } from '../../../services/workbasketservice.service';
import { IconTypeComponent, ICONTYPES } from '../../../shared/type-icon/icon-type.component';

@Component({
  selector: 'workbasket-information',
  templateUrl: './workbasket-information.component.html',
  styleUrls: ['./workbasket-information.component.scss']
})
export class WorkbasketInformationComponent implements OnInit {

  @Input()
  workbasket: Workbasket;
  allTypes: Map<string, string>;
  constructor(private service: WorkbasketService) { 
    this.allTypes = IconTypeComponent.allTypes;
  }

  ngOnInit() {
  }

  selectType(type: ICONTYPES){
    this.workbasket.type = type;
  }
}
