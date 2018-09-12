import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Task} from 'app/workplace/models/task';
import {Classification} from '../../../models/classification';
import {ClassificationsService} from '../../../services/classifications/classifications.service';

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
}
