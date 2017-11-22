import { Component, OnInit, EventEmitter } from '@angular/core';
import { Workbasket } from '../model/workbasket';

@Component({
  selector: 'app-workbasketeditor',
  inputs: ['workbasket'],
  outputs: ['workbasketSaved'],
  templateUrl: './workbasketeditor.component.html',
  styleUrls: ['./workbasketeditor.component.css']
})
export class WorkbasketeditorComponent implements OnInit {
  public workbasket: Workbasket;
  public workbasketSaved: EventEmitter<Workbasket> = new EventEmitter();

  constructor() { }

  ngOnInit() {
    this.workbasket = new Workbasket("", "", "", "", "", "", null);
  }

  onSubmit() {
    // TODO save values
    console.log("changed " + this.workbasket.name);
    this.workbasketSaved.next(this.workbasket);
  }
}
