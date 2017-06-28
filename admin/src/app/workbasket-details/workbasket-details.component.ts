import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Workbasket } from '../model/workbasket';
import { WorkbasketserviceService } from '../services/workbasketservice.service';

@Component({
  selector: 'app-workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.css']
})
export class WorkbasketDetailsComponent implements OnInit {

  @Input()
  workbasket: Workbasket;
  workbasketClone: Workbasket;

  allWorkbasket: Workbasket[];
  editMode: boolean = false;

  @Output()
  onSaved = new EventEmitter<Workbasket>();

  constructor(private service: WorkbasketserviceService) { }

  ngOnInit() {
    this.workbasketClone = { ...this.workbasket };
  }

  ngOnChanges() {
    this.workbasketClone = { ...this.workbasket };
    this.editMode = false;
  }

  onEdit() {
    this.editMode = true;
  }

  onSave() {
    this.onSaved.emit(this.workbasketClone);
    this.editMode = false;
  }
}
