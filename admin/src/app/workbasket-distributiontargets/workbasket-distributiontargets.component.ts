import { Component, OnInit, Input } from '@angular/core';
import { Workbasket } from '../model/workbasket';
import { WorkbasketserviceService } from '../services/workbasketservice.service'
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-workbasket-distributiontargets',
  templateUrl: './workbasket-distributiontargets.component.html',
  styleUrls: ['./workbasket-distributiontargets.component.css']
})
export class WorkbasketDistributiontargetsComponent implements OnInit {

  @Input()
  workbasket: Workbasket;
  workbaskets: Workbasket[];
  public alerts: any = [];

  constructor(private service: WorkbasketserviceService) { }

  ngOnInit() {
    this.prepareData();
  }

  ngOnChange() {
    this.prepareData();
  }

  prepareData() {
    this.service.getAllWorkBaskets().subscribe(resultList => {
      this.workbaskets = resultList;
    });
  }

  onAdd(w: Workbasket) {
    if (this.workbasket.distributionTargets.length > 0) {
      let found: boolean = false;
      for (var i = 0, len = this.workbasket.distributionTargets.length; i < len; i++) {
        if (this.workbasket.distributionTargets[i] === w.id) {
          found = true;
          break;
        }
      }

      if (!found) {
        this.onSaved(w, true);
      } else {
        this.alerts = [{
          type: "danger",
          msg: "This workbasket is already mapped!"
        }];
      }
    } else {
      this.onSaved(w, true);
    }
  }

  onDelete(id: string) {
    this.onSaved(this.resolveObject(id), false);
  }

  // get workbasket name
  resolveName(id: string): any {
    if (this.workbaskets != null) {
      return this.workbaskets.filter(item => item.id === id)[0].name;
    }
  }

  // create an Workbasket
  resolveObject(id: string): any {
    if (this.workbaskets != null) {
      return this.workbaskets.filter(item => item.id === id)[0];
    }
  }

  onSaved(w: Workbasket, isUpdate: boolean) {
    if (w != null) {
      // add changes
      if (isUpdate) {
        this.workbasket.distributionTargets.push(w.id);
      } else {
        let index = this.workbasket.distributionTargets.indexOf(w.id);
        this.workbasket.distributionTargets.splice(index, 1);
      }

      // try to save changes
      this.service.updateWorkbasket(this.workbasket).subscribe(result => {
        this.workbasket.id = result.id;
        this.workbasket.name = result.name;
        this.workbasket.description = result.description;
        this.workbasket.owner = result.owner;
        this.workbasket.distributionTargets = result.distributionTargets;
      }, (err) => {
        this.alerts = [{
          type: "danger",
          msg: "You are not authorized."
        }];
        // reset changes
        if (isUpdate) {
          this.workbasket.distributionTargets.pop();
        } else {
          this.workbasket.distributionTargets.push(w.id);
        }
      });
    }
  }
}
