import { Component, OnInit, EventEmitter } from '@angular/core';
import { Workbasket } from '../model/workbasket';
import { WorkbasketserviceService } from '../services/workbasketservice.service'
import { ActivatedRoute, Params } from '@angular/router';

@Component({
  selector: 'app-workbasketlist',
  outputs: ['selectedWorkbasket'],
  templateUrl: './workbasketlist.component.html',
  styleUrls: ['./workbasketlist.component.css'],
  providers: [WorkbasketserviceService]
})
export class WorkbasketlistComponent implements OnInit {
  public selectedWorkbasket: EventEmitter<Workbasket> = new EventEmitter();

  workbasket: Workbasket = this.getEmptyObject();
  selected: Workbasket = this.getEmptyObject();
  editing: Workbasket = this.getEmptyObject();
  isEditing: boolean = false;

  wbClicked = true;
  authClicked = false;
  dtClicked = false

  workbaskets = [];

  public alerts: any = [];

  constructor(private service: WorkbasketserviceService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.service.getAllWorkBaskets().subscribe(resultList => {
      this.workbaskets = resultList;
    });
  }

  onDelete(workbasket: Workbasket) {
    this.service.deleteWorkbasket(workbasket.id).subscribe(result => {
      var index = this.workbaskets.indexOf(workbasket);
      if (index > -1) {
        this.workbaskets.splice(index, 1);
      }
    });
  }

  onAdd() {
    this.service.createWorkbasket(this.workbasket).subscribe(result => {
      this.workbaskets.push(result);
      this.onClear();
    });
  }

  onEdit(workbasket: Workbasket) {
    this.editing = { ...workbasket };
    this.isEditing = true;
  }

  onSelect(workbasket: Workbasket) {
    if (!this.isEditing) {
      this.selected = workbasket;
    }
  }

  onClear() {
    this.workbasket.id = "";
    this.workbasket.name = "";
    this.workbasket.description = "";
    this.workbasket.owner = "";
  }

  onSave() {
    if (this.isEditing) {
      this.service.updateWorkbasket(this.editing).subscribe(result => {
        this.selected.id = result.id;
        this.selected.name = result.name;
        this.selected.description = result.description;
        this.selected.owner = result.owner;
      }, (err) => {
        this.alerts = [{
          type: "danger",
          msg: "You are not authorized."
        }]
      });
    }
    this.isEditing = false;
    this.editing = this.getEmptyObject();
  }

  onCancel() {
    this.editing = this.getEmptyObject();
    this.isEditing = false;
  }

  getEmptyObject() {
    return new Workbasket("", "", "", "", "", "", null);
  }

  onClickWB() {
    this.wbClicked = !this.wbClicked;
    this.authClicked = false;
    this.dtClicked = false;
  }

  onClickAuth() {
    this.authClicked = !this.authClicked;
    this.wbClicked = false;
    this.dtClicked = false;
  }

  onClickDt() {
    this.dtClicked = !this.dtClicked;
    this.wbClicked = false;
    this.authClicked = false;
  }
}
