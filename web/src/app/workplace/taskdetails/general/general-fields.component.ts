import { Component, EventEmitter, Input, OnInit, Output, ViewChild, SimpleChanges, OnChanges } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Classification } from '../../../models/classification';
import { ClassificationsService } from '../../../services/classifications/classifications.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'taskana-task-details-general-fields',
  templateUrl: './general-fields.component.html',
  styleUrls: ['./general-fields.component.scss']
})
export class TaskdetailsGeneralFieldsComponent implements OnInit, OnChanges {

  @Input()
  task: Task;
  @Output() taskChange: EventEmitter<Task> = new EventEmitter<Task>();

  @Input()
  saveToggleTriggered: boolean;
  @Output() formValid: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output() classificationsReceived: EventEmitter<Classification[]> = new EventEmitter<Classification[]>();

  @ViewChild('TaskForm')
  taskForm: NgForm;

  toogleValidationMap = new Map<string, boolean>();
  requestInProgress = false;
  classifications: Classification[] = undefined;

  ownerField = this.customFieldsService.getCustomField(
    'Owner',
    'tasks.information.owner'
  );

  constructor(
    private classificationService: ClassificationsService,
    private customFieldsService: CustomFieldsService,
    private formsValidatorService: FormsValidatorService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.saveToggleTriggered && changes.saveToggleTriggered.currentValue !== changes.saveToggleTriggered.previousValue) {
      this.validate();
    }
  }

  ngOnInit() {
    this.requestInProgress = true;
    this.classificationService.getClassifications().subscribe(classificationList => {
      this.requestInProgress = false;
      this.classifications = classificationList;
      if (classificationList.length > 0) { this.task.classificationSummaryResource = classificationList[0]; }
      this.classificationsReceived.emit(this.classifications);
    });
  }

  selectClassification(classification: Classification) {
    this.task.classificationSummaryResource = classification;
  }

  validate() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService
      .validateFormInformation(this.taskForm, this.toogleValidationMap)
      .then(value => {
        if (value) {
          this.formValid.emit(true);
        }
      });
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.taskForm, field);
  }

}
