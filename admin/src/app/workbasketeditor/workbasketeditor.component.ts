import { Component, OnInit, EventEmitter } from '@angular/core';
import { WorkbasketSummary } from '../model/workbasketSummary';

@Component({
  selector: 'app-workbasketeditor',
  inputs: ['workbasket'],
  outputs: ['workbasketSaved'],
  templateUrl: './workbasketeditor.component.html',
  styleUrls: ['./workbasketeditor.component.css']
})
export class WorkbasketeditorComponent implements OnInit {
  public workbasket: WorkbasketSummary;
  public workbasketSaved: EventEmitter<WorkbasketSummary> = new EventEmitter();

  constructor() { }

  ngOnInit() {
    this.workbasket = new WorkbasketSummary("", "", "", "", "", "", "", "", "", "", "", "");
  }

  onSubmit() {
    // TODO save values
    console.log("changed " + this.workbasket.name);
    this.workbasketSaved.next(this.workbasket);
  }
}
