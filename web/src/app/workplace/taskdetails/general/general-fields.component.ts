import { Component, EventEmitter, Input, OnInit, Output, ViewChild, SimpleChanges, OnChanges, HostListener } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Classification } from '../../../models/classification';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { NgForm } from '@angular/forms';
import { DomainService } from 'app/services/domain/domain.service';

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
    private formsValidatorService: FormsValidatorService,
    private domainService: DomainService) {
  }

  ngOnInit() {
    this.getClassificationByDomain();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.saveToggleTriggered && changes.saveToggleTriggered.currentValue !== changes.saveToggleTriggered.previousValue) {
      this.validate();
    }
  }

  selectClassification(classification: Classification) {
    this.task.classificationSummaryResource = classification;
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.taskForm, field);
  }

  updateDate($event) {
    if (new Date(this.task.due).toISOString() !== $event) {
      this.task.due = $event;
    }
  }

  private validate() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService
      .validateFormInformation(this.taskForm, this.toogleValidationMap)
      .then(value => {
        if (value) {
          this.formValid.emit(true);
        }
      });
  }

  private changedClassification(itemSelected: any) {
    this.task.classificationSummaryResource = itemSelected;
  }

  private async getClassificationByDomain() {
    this.requestInProgress = true;
    this.classifications = (await this.classificationService.getClassificationsByDomain(this.domainService.getSelectedDomainValue()))
      ._embedded.classificationSummaryResourceList;
    this.requestInProgress = false;
  }
}
