import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {Classification} from '../../../models/classification';
import {ClassificationsService} from '../../../services/classifications/classifications.service';
import {TaskanaDate} from '../../../shared/util/taskana.date';

@Component({
  selector: 'taskana-task-details-general-fields',
  templateUrl: './general-fields.component.html',
  styleUrls: ['./general-fields.component.scss']
})
export class TaskdetailsGeneralFieldsComponent implements OnInit {

  task: Task;

  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();
  @Output() classificationsReceived: EventEmitter<Classification[]> = new EventEmitter<Classification[]>();

  requestInProgress = false;
  selectedClassification: Classification = new Classification();
  classifications: Classification[] = undefined;

  constructor(private classificationService: ClassificationsService) {
  }

  ngOnInit() {
    this.requestInProgress = true;
    this.classificationService.getClassifications().subscribe(classificationList => {
      this.requestInProgress = false;
      this.classifications = classificationList;
      this.classificationsReceived.emit(this.classifications);
    });
  }

  @Input()
  set _task(task: Task) {
    this.task = task;
    this.selectedClassification = task.classificationSummaryResource;
  }

  selectClassification(classification: Classification) {
    this.selectedClassification = classification;
    this.task.classificationSummaryResource = classification;
  }

  validateDate(date: string) {
    if (/^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\d|3[0-1])$/.test(date)) {
      const bits = date.split('-').map(element => parseInt(element, 10));
      const realDate = new Date(bits[0], bits[1], bits[2]);
      const currentDate = TaskanaDate.convertSimpleDate(new Date()).toString();
      if (realDate && date > currentDate) {
        console.log('YAY!');
        this.task.due = TaskanaDate.getISODate(new Date(date)).toString();
      }
    }
  }
}
