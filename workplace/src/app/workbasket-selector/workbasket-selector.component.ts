import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { RestConnectorService } from '../services/rest-connector.service';
import { DataService } from '../services/data.service';
import { Task } from '../model/task';
import { Workbasket } from '../model/workbasket';

@Component({
  selector: 'workbasket-selector',
  templateUrl: './workbasket-selector.component.html',
  styleUrls: ['./workbasket-selector.component.scss']
})
export class SelectorComponent implements OnInit {

  @Output() tasks = new EventEmitter<Task[]>();

  autoCompleteData: string[] = new Array;
  result: string;
  resultKey: string;
  workbaskets: Workbasket[];

  constructor(private restConnectorService: RestConnectorService, private dataService: DataService) { }

  ngOnInit() {
    this.restConnectorService.getAllWorkBaskets().subscribe( w => {
      this.workbaskets = w;
      this.workbaskets.forEach(workbasket => {
        this.autoCompleteData.push(workbasket.name);
      });
    });
    if (this.dataService.workbasketKey) {
      this.getTasks(this.dataService.workbasketKey);
      this.result = this.dataService.workbasketName;
    }
  }

  searchBasket() {
    if (this.workbaskets) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.result) {
          this.resultKey = workbasket.key;
        }
      });
      this.getTasks(this.resultKey);
      this.dataService.workbasketKey = this.resultKey;
      this.dataService.workbasketName = this.result;
    }
  }

  getTasks(workbasketKey: string) {
    this.restConnectorService.findTaskWithWorkbaskets(workbasketKey).subscribe(
                       tasks2 => {this.tasks.next(tasks2)});
  }
}
