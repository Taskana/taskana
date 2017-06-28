import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { WorkbasketserviceService } from '../services/workbasketservice.service';
import { WorkbasketAuthorization } from '../model/workbasket-authorization';
import { Workbasket } from '../model/workbasket';

@Component({
  selector: 'app-workbasket-authorization',
  templateUrl: './workbasket-authorization.component.html',
  styleUrls: ['./workbasket-authorization.component.css'],
  providers: [WorkbasketserviceService]
})
export class WorkbasketAuthorizationComponent implements OnInit {

  @Input()
  workbasket: Workbasket;

  workbasketAuthorization: WorkbasketAuthorization = this.getEmptyObject();
  selected: WorkbasketAuthorization = this.getEmptyObject();
  editing: WorkbasketAuthorization = this.getEmptyObject();
  isEditing: boolean = false;

  constructor(private service: WorkbasketserviceService, private route: ActivatedRoute) { }

  workbasketAuthorizations: WorkbasketAuthorization[];

  ngOnInit() {
    this.route.params.switchMap((params: Params) => this.service.getAllWorkBasketAuthorizations(params['id']))
      .subscribe(resultList => {
        this.workbasketAuthorizations = resultList;
      });
  }

  getEmptyObject() {
    return new WorkbasketAuthorization("", "", "", "", false, false, false, false, false);
  }

  onDelete(workbasket: WorkbasketAuthorization) {
    this.service.deleteWorkBasketAuthorization(workbasket).subscribe(result => {
      var index = this.workbasketAuthorizations.indexOf(workbasket);
      if (index > -1) {
        this.workbasketAuthorizations.splice(index, 1);
      }
    });
  }

  onAdd() {
    console.log(this.workbasketAuthorization);
    this.workbasketAuthorization.workbasketId = this.workbasket.id;
    this.service.createWorkBasketAuthorization(this.workbasketAuthorization).subscribe(result => {
      this.workbasketAuthorizations.push(result);
      this.onClear();
    });
  }

  onEdit(workbasketAuthorizations: WorkbasketAuthorization) {
    this.editing = { ...workbasketAuthorizations };
    this.isEditing = true;
  }

  onSelect(workbasketAuthorizations: WorkbasketAuthorization) {
    if (!this.isEditing) {
      this.selected = workbasketAuthorizations;
    }
  }

  onClear() {
    this.workbasketAuthorization.id = "";
    this.workbasketAuthorization.workbasketId = "";
    this.workbasketAuthorization.userId = "";
    this.workbasketAuthorization.groupId = "";
    this.workbasketAuthorization.read = false;
    this.workbasketAuthorization.open = false;
    this.workbasketAuthorization.append = false;
    this.workbasketAuthorization.transfer = false;
    this.workbasketAuthorization.distribute = false;
  }

  onSave() {
    if (this.isEditing) {
      this.service.updateWorkBasketAuthorization(this.editing).subscribe(result => {
        this.selected.id = result.id;
        this.selected.workbasketId = result.workbasketId;
        this.selected.userId = result.userId;
        this.selected.groupId = result.groupId;
        this.selected.read = result.read;
        this.selected.open = result.open;
        this.selected.append = result.append;
        this.selected.transfer = result.transfer;
        this.selected.distribute = result.distribute;
      });
    }
    this.isEditing = false;
    this.editing = this.getEmptyObject();
  }

  onCancel() {
    this.editing = this.getEmptyObject();
    this.isEditing = false;
  }
}
