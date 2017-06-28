import { Component, OnInit } from '@angular/core';
import { Workbasket } from '../model/workbasket';

@Component({
  selector: 'app-workbasketadministration',
  templateUrl: './workbasketadministration.component.html',
  styleUrls: ['./workbasketadministration.component.css']
})
export class WorkbasketadministrationComponent implements OnInit {
  selectedWorkbasket: Workbasket;

  constructor() { }

  ngOnInit() {
  }

  onWorkbasketSelected(workbasket: Workbasket) {
    console.log("got new selected workbasket: " + workbasket.id);
    this.selectedWorkbasket = workbasket;
  }

  onWorkbasketSaved(workbasket: Workbasket) {
    console.log("got saved workbasket: " + workbasket);
  }

}
